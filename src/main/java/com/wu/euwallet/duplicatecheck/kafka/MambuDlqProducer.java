package com.wu.euwallet.duplicatecheck.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.euwallet.duplicatecheck.exception.utils.WUServiceExceptionUtils;
import com.wu.euwallet.duplicatecheck.model.common.kafka.ErrorEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MambuDlqProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // TODO: set DLQ topic name in properties and inject via config class
    private static final String DLQ_TOPIC = "mambu.dlq.topic";

    public void send(ErrorEvent errorEvent) throws Exception {
        try {
            String message = objectMapper.writeValueAsString(errorEvent);
            kafkaTemplate.send(DLQ_TOPIC, message);
            log.info("Published error event to DLQ: {}", message);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize ErrorEvent", e);
            throw WUServiceExceptionUtils.buildJsonProcessingException("DLQ_SERIALIZATION_FAILED", e.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected failure in DLQ producer", ex);
            throw WUServiceExceptionUtils.buildKafkaException("DLQ_KAFKA_ERROR", ex.getMessage());
        }
    }
}
