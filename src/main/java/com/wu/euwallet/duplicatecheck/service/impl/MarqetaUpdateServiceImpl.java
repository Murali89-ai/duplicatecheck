package com.wu.euwallet.duplicatecheck.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.euwallet.duplicatecheck.adaptor.Marqeta;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUExceptionType;
import com.wu.euwallet.duplicatecheck.exception.utils.WUServiceExceptionUtils;
import com.wu.euwallet.duplicatecheck.kafka.ProfileUpdateKafkaEventPublisher;
import com.wu.euwallet.duplicatecheck.model.common.kafka.TransactionData;
import com.wu.euwallet.duplicatecheck.model.request.ProfileUpdateRequest;
import com.wu.euwallet.duplicatecheck.service.MarqetaUpdateService;
import com.wu.euwallet.duplicatecheck.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarqetaUpdateServiceImpl implements MarqetaUpdateService {

    private final Marqeta marqeta;
    private final ProfileUpdateKafkaEventPublisher kafkaPublisher;
    private final ObjectMapper objectMapper;

    @Override
    public void updateCard(ProfileUpdateRequest request, TransactionData transactionData) {
        try {
            // Validate request before sending to Marqeta
            ValidationUtils.validate(request);

            // Call Marqeta adaptor
            marqeta.updateCustomer(request, transactionData);

            log.info("Successfully updated card info in Marqeta for partyId {}", request.getPartyId());

        } catch (Exception ex) {
            log.error("Failed to update card in Marqeta for partyId {}", request.getPartyId(), ex);

            // Publish failure event to Kafka
            kafkaPublisher.publishMarqetaFailureEvent(request, transactionData, ex.getMessage());

            throw WUServiceExceptionUtils.buildWUServiceException(
                    WUExceptionType.MARQETA_UPDATE_FAILED,
                    "Failed to update card in Marqeta",
                    ex
            );
        }
    }
}
