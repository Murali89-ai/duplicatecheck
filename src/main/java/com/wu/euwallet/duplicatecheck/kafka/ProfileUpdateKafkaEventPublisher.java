package com.wu.euwallet.duplicatecheck.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.euwallet.duplicatecheck.config.KafkaTopicProperties;
import com.wu.euwallet.duplicatecheck.constants.AppConstants;
import com.wu.euwallet.duplicatecheck.model.common.kafka.TransactionData;
import com.wu.euwallet.duplicatecheck.model.request.ProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileUpdateKafkaEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaTopicProperties kafkaTopicProperties;

    public void publishProfileUpdateSuccessEvent(ProfileUpdateRequest request, TransactionData txData) {
        publish(kafkaTopicProperties.getProfileUpdateSuccess(), request, txData, AppConstants.EVENT_TYPE_PROFILE_UPDATE_SUCCESS);
    }

    public void publishDuplicateErrorEvent(ProfileUpdateRequest request, TransactionData txData, String reason) {
        publish(kafkaTopicProperties.getProfileUpdateDuplicate(), request, txData, AppConstants.EVENT_TYPE_DUPLICATE_FOUND, reason);
    }

    public void publishMarqetaFailureEvent(ProfileUpdateRequest request, TransactionData txData, String error) {
        publish(kafkaTopicProperties.getMarqetaFailure(), request, txData, AppConstants.EVENT_TYPE_MARQETA_FAILURE, error);
    }

    public void publishBizFailureEvent(ProfileUpdateRequest request, TransactionData txData, String error) {
        publish(kafkaTopicProperties.getBizFailure(), request, txData, AppConstants.EVENT_TYPE_BIZ_FAILURE, error);
    }

    private void publish(String topic, ProfileUpdateRequest request, TransactionData txData, String eventType) {
        publish(topic, request, txData, eventType, null);
    }

    private void publish(String topic, ProfileUpdateRequest request, TransactionData txData,
                         String eventType, String failureReason) {
        try {
            String payload = objectMapper.writeValueAsString(request);

            RecordHeaders headers = new RecordHeaders();
            headers.add(new RecordHeader("transactionId", value(txData.getTransactionId())));
            headers.add(new RecordHeader("correlationId", value(txData.getCorrelationId())));
            headers.add(new RecordHeader("applicationId", value(txData.getApplicationId())));
            headers.add(new RecordHeader("channel", value(txData.getChannel())));
            headers.add(new RecordHeader("eventType", value(eventType)));

            if (failureReason != null) {
                headers.add(new RecordHeader("failureReason", value(failureReason)));
            }

            ProducerRecord<String, String> record = new ProducerRecord<>(topic, null, null, null, payload, headers);

            kafkaTemplate.send(record);
            log.info("Kafka event published to topic [{}] for partyId [{}]", topic, request.getPartyId());

        } catch (Exception ex) {
            log.error("Failed to publish Kafka event to topic: " + topic, ex);
        }
    }

    private byte[] value(String val) {
        return val != null ? val.getBytes(StandardCharsets.UTF_8) : null;
    }
}
