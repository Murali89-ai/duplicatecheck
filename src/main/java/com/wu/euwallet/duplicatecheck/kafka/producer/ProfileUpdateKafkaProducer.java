package com.wu.euwallet.duplicatecheck.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.euwallet.duplicatecheck.config.KafkaTopicsProperties;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUExceptionType;
import com.wu.euwallet.duplicatecheck.exception.utils.WUServiceExceptionUtils;
import com.wu.euwallet.duplicatecheck.model.kafka.DuplicateCheckKafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileUpdateKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTopicsProperties kafkaTopics;
    private final ObjectMapper mapper;

    /* ------------------------------------------------------------------ */
    /* PUBLIC HELPERS                                                     */
    /* ------------------------------------------------------------------ */

    public void sendBusinessEvent(DuplicateCheckKafkaEvent event, Headers headers) {
        sendEvent(kafkaTopics.getBusinessTopic(), event, headers);
    }

    public void sendErrorEvent(DuplicateCheckKafkaEvent event, Headers headers) {
        sendEvent(kafkaTopics.getErrorTopic(), event, headers);
    }

    /* ------------------------------------------------------------------ */
    /* INTERNAL LOW-LEVEL SEND                                            */
    /* ------------------------------------------------------------------ */

    private void sendEvent(String topic, DuplicateCheckKafkaEvent event, Headers headers) {

        String payload;
        try {
            payload = mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("❌ Error serialising Kafka event", e);
            throw WUServiceExceptionUtils.buildWUServiceException(WUExceptionType.INTERNAL_ERROR, e.getMessage());
        }

        ProducerRecord<String, String> record = new ProducerRecord<>(topic, payload);
        headers.forEach(record.headers()::add);

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(record);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("✅ Kafka OK – topic [{}] partition [{}] offset [{}]",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("❌ Kafka send FAILED for topic [{}]", topic, ex);
            }
        });
    }
}
