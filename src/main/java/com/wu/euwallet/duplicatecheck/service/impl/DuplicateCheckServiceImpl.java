package com.wu.euwallet.duplicatecheck.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wu.euwallet.duplicatecheck.adaptor.BizAdaptor;
import com.wu.euwallet.duplicatecheck.adaptor.BlazeAdaptor;
import com.wu.euwallet.duplicatecheck.adaptor.MambuAdaptor;
import com.wu.euwallet.duplicatecheck.config.KafkaTopicsConfig;
import com.wu.euwallet.duplicatecheck.constants.DuplicateCheckConstants;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUExceptionType;
import com.wu.euwallet.duplicatecheck.exception.utils.WUServiceExceptionUtils;
import com.wu.euwallet.duplicatecheck.kafka.KafkaHeadersUtil;
import com.wu.euwallet.duplicatecheck.kafka.producer.ProfileUpdateKafkaProducer;
import com.wu.euwallet.duplicatecheck.model.kafka.DuplicateCheckKafkaEvent;
import com.wu.euwallet.duplicatecheck.model.request.ProfileUpdateRequest;
import com.wu.euwallet.duplicatecheck.model.request.biz.PinChangeRequest;
import com.wu.euwallet.duplicatecheck.model.request.blaze.RiskCheckRequest;
import com.wu.euwallet.duplicatecheck.model.response.ProfileUpdateResponse;
import com.wu.euwallet.duplicatecheck.service.DuplicateCheckService;
import com.wu.euwallet.duplicatecheck.service.HttpService;
import com.wu.euwallet.duplicatecheck.service.MarqetaUpdateService;
import com.wu.euwallet.duplicatecheck.transformer.UcdUpdateRequestBuilder;
import com.wu.euwallet.duplicatecheck.validation.RequestValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DuplicateCheckServiceImpl implements DuplicateCheckService {

    private final RequestValidator validator;
    private final BlazeAdaptor blazeAdaptor;
    private final BizAdaptor bizAdaptor;
    private final MambuAdaptor mambuAdaptor;
    private final MarqetaUpdateService marqetaUpdateService;
    private final ProfileUpdateKafkaProducer kafkaProducer;
    private final KafkaTopicsConfig kafkaTopics;
    private final ObjectMapper mapper;
    private final HttpService httpService;
    private final UcdUpdateRequestBuilder ucdUpdateRequestBuilder;

    @Override
    public ProfileUpdateResponse updateProfile(@Valid ProfileUpdateRequest request) {
        String correlationId = UUID.randomUUID().toString();
        String externalRefId = request.getExternalReferenceId();

        try {
            validator.notBlank(request.getCustomerNumber(), "Customer number is required");

            // Blaze Risk Check
            RiskCheckRequest riskRequest = RiskCheckRequest.builder()
                    .customerNumber(request.getCustomerNumber())
                    .build();
            blazeAdaptor.performRiskCheck(riskRequest);

            // Biz PIN Change
            if (request.getNewPin() != null && !request.getNewPin().isBlank()) {
                bizAdaptor.changePin(PinChangeRequest.builder()
                        .cardNumber(request.getCustomerNumber())
                        .newPin(request.getNewPin())
                        .build());
            }

            // Mambu Update
            mambuAdaptor.updateCustomer(request);

            // Marqeta Card Update
            marqetaUpdateService.process(mapper.writeValueAsString(request));

            // UCD Update via HTTP
            String ucdPayload = mapper.writeValueAsString(ucdUpdateRequestBuilder.buildUpdate(request,correlationId));
            httpService.callUcdPatchEndpoint(ucdPayload);

            // Success Event to Kafka
            DuplicateCheckKafkaEvent event = DuplicateCheckKafkaEvent.builder()
                    .correlationId(correlationId)
                    .externalRefId(externalRefId)
                    .status(DuplicateCheckConstants.DUPLICATE_CHECK_SUCCESS_CODE)
                    .message("Profile update successful")
                    .sourceSystem("DUPLICATE_CHECK")
                    .eventType("BUSINESS_EVENT")
                    .eventTime(nowIso())
                    .build();

            Headers headers = KafkaHeadersUtil.buildStandardHeaders(correlationId, externalRefId, "duplicate-check-svc");
            kafkaProducer.sendBusinessEvent(event, headers);

            return ProfileUpdateResponse.builder()
                    .status("SUCCESS")
                    .message("Profile updated successfully")
                    .correlationId(correlationId)
                    .build();

        } catch (Exception ex) {
            log.error("Profile update failed", ex);

            DuplicateCheckKafkaEvent errorEvent = DuplicateCheckKafkaEvent.builder()
                    .correlationId(correlationId)
                    .externalRefId(externalRefId)
                    .status(DuplicateCheckConstants.DUPLICATE_CHECK_ERROR_CODE)
                    .message(ex.getMessage())
                    .sourceSystem("DUPLICATE_CHECK")
                    .eventType("ERROR_EVENT")
                    .eventTime(nowIso())
                    .build();

            Headers headers = KafkaHeadersUtil.buildStandardHeaders(correlationId, externalRefId, "duplicate-check-svc");
            kafkaProducer.sendErrorEvent(errorEvent, headers);

            throw WUServiceExceptionUtils.buildWUServiceException(WUExceptionType.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    private static String nowIso() {
        return OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
