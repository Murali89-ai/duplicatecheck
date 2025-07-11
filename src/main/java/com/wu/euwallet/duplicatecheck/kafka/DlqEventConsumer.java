package com.wu.euwallet.duplicatecheck.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.euwallet.duplicatecheck.adaptor.Mambu;
import com.wu.euwallet.duplicatecheck.adaptor.Marqeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DlqEventConsumer {

    private final ObjectMapper objectMapper;
    private final Marqeta marqeta;
    private final Mambu mambu;

    @KafkaListener(topics = "${kafka.topic.dlq.marqeta}", groupId = "dupcheck-dlq-group")
    public void processMarqetaDlq(ConsumerRecord<String, String> record) {
        log.info("Consumed Marqeta DLQ message: {}", record.value());
        try {
            JsonNode payload = objectMapper.readTree(record.value());
            marqeta.retry(payload); // delegate to adapter
        } catch (Exception e) {
            log.error("Failed to process Marqeta DLQ message", e);
        }
    }

    @KafkaListener(topics = "${kafka.topic.dlq.mambu}", groupId = "dupcheck-dlq-group")
    public void processMambuDlq(ConsumerRecord<String, String> record) {
        log.info("Consumed Mambu DLQ message: {}", record.value());
        try {
            JsonNode payload = objectMapper.readTree(record.value());
            mambu.retry(payload); // delegate to adapter
        } catch (Exception e) {
            log.error("Failed to process Mambu DLQ message", e);
        }
    }
}
