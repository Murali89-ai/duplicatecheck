package com.wu.euwallet.duplicatecheck.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.euwallet.duplicatecheck.adaptor.Biz;
import com.wu.euwallet.duplicatecheck.adaptor.Blaze;
import com.wu.euwallet.duplicatecheck.adaptor.Mambu;
import com.wu.euwallet.duplicatecheck.adaptor.Marqeta;
import com.wu.euwallet.duplicatecheck.adaptor.Ping;
import com.wu.euwallet.duplicatecheck.adaptor.RAC;
import com.wu.euwallet.duplicatecheck.adaptor.SFMC;
import com.wu.euwallet.duplicatecheck.adaptor.UCD;
import com.wu.euwallet.duplicatecheck.dto.DuplicateCheckResponse;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUExceptionType;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUServiceException;
import com.wu.euwallet.duplicatecheck.exception.utils.WUServiceExceptionUtils;
import com.wu.euwallet.duplicatecheck.kafka.ProfileUpdateKafkaEventPublisher;
import com.wu.euwallet.duplicatecheck.model.common.kafka.TransactionData;
import com.wu.euwallet.duplicatecheck.model.request.ProfileUpdateRequest;
import com.wu.euwallet.duplicatecheck.model.request.biz.BizChangePinRequest;
import com.wu.euwallet.duplicatecheck.model.request.mambu.MambuUpdateRequest;
import com.wu.euwallet.duplicatecheck.model.request.ucd.UcdRequest;
import com.wu.euwallet.duplicatecheck.service.DuplicateCheckService;
import com.wu.euwallet.duplicatecheck.service.MarqetaUpdateService;
import com.wu.euwallet.duplicatecheck.transformer.BizRequestBuilder;
import com.wu.euwallet.duplicatecheck.transformer.ProfileUpdateRequestTransformer;
import com.wu.euwallet.duplicatecheck.transformer.UcdUpdateRequestBuilder;
import com.wu.euwallet.duplicatecheck.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DuplicateCheckServiceImpl implements DuplicateCheckService {

    private final UCD ucd;
    private final Marqeta marqeta;
    private final Biz biz;
    private final Blaze blaze;
    private final SFMC sfmc;
    private final Mambu mambu;
    private final Ping ping;
    private final RAC rac;

    private final MarqetaUpdateService marqetaUpdateService;
    private final ProfileUpdateKafkaEventPublisher kafkaPublisher;
    private final UcdUpdateRequestBuilder ucdRequestBuilder;
    private final ProfileUpdateRequestTransformer requestTransformer;
    private final ObjectMapper objectMapper;

    @Override
    public DuplicateCheckResponse processProfileUpdate(ProfileUpdateRequest request, TransactionData txData) {
        try {
            // Validate the incoming request
            ValidationUtils.validate(request);

            // Step 1: UCD Duplicate Check
            UcdRequest ucdRequest = ucdRequestBuilder.buildRequest(request, txData);
            JsonNode jsonNode = ucd.checkForDuplicate(ucdRequest);

            if (jsonNode != null && jsonNode.has("duplicate") && Boolean.TRUE.equals(jsonNode.get("duplicate").asBoolean())) {
                log.warn("Duplicate detected by UCD for partyId: {}", request.getPartyId());
                kafkaPublisher.publishDuplicateErrorEvent(request, txData, jsonNode.toString());
                throw new WUServiceException(WUExceptionType.DUPLICATE_PROFILE_UPDATE, jsonNode.toString());
            }

            // Step 2: Marqeta Update
            marqetaUpdateService.updateCard(request, txData);

            // Step 3: Biz PIN Update
            BizChangePinRequest bizRequest = BizRequestBuilder.buildChangePinRequest(request);
            biz.bizChangePin(bizRequest, txData);

            // Step 4: Blaze Integration
            blaze.evaluateRules(request, txData);

            // Step 5: Ping Update
            ping.updateProfile(request, txData);

            // Step 6: RAC Integration
            rac.sendProfileUpdate(request, txData);

            // Step 7: SFMC
            sfmc.sendCommunication(request, txData);

            // Step 8: Mambu Notification
            MambuUpdateRequest mambuRequest = requestTransformer.toMambuRequest(request, txData);
            mambu.notifyMambu(mambuRequest,txData);

            // Step 9: Kafka Success Event
            kafkaPublisher.publishProfileUpdateSuccessEvent(request, txData);

            return DuplicateCheckResponse.builder()
                    .partyId(request.getPartyId())
                    .status("SUCCESS")
                    .message("Profile update successful")
                    .timestamp(LocalDateTime.now().toString())
                    .build();

        } catch (Exception ex) {
            log.error("Error in processing profile update for partyId {}", request.getPartyId(), ex);
            throw WUServiceExceptionUtils.buildWUServiceException(WUExceptionType.SERVICE_FAILED,
                    "Profile update processing failed", ex);
        }
    }
}
