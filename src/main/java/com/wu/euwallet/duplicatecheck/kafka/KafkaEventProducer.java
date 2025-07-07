package com.wu.euwallet.duplicatecheck.kafka;

import com.wu.euwallet.duplicatecheck.model.kafka.DuplicateCheckKafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventProducer {

    private final KafkaTemplate<String, DuplicateCheckKafkaEvent> kafkaTemplate;

    public void sendEvent(String topic, DuplicateCheckKafkaEvent event) {
        try {
            kafkaTemplate.send(topic, event);
            log.info("Published Kafka event to topic [{}]: {}", topic, event);
        } catch (Exception ex) {
            log.error("Failed to publish Kafka event to topic [{}]: {}", topic, event, ex);
            throw new RuntimeException("Kafka publish failed", ex);
        }
    }
}
