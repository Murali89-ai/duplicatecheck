package com.wu.euwallet.duplicatecheck.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.euwallet.duplicatecheck.model.kafka.DuplicateCheckKafkaEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProducerProperties props;
    private final ObjectMapper objectMapper;

    // 1. ProducerFactory for String payload (for ProfileUpdateKafkaProducer)
    @Bean
    public ProducerFactory<String, String> stringProducerFactory() {
        Map<String, Object> cfg = baseConfig();
        return new DefaultKafkaProducerFactory<>(cfg, new StringSerializer(), new StringSerializer());
    }

    @Bean
    public KafkaTemplate<String, String> stringKafkaTemplate() {
        return new KafkaTemplate<>(stringProducerFactory());
    }

    // 2. ProducerFactory for DuplicateCheckKafkaEvent payload
    @Bean
    public ProducerFactory<String, DuplicateCheckKafkaEvent> eventProducerFactory() {
        Map<String, Object> cfg = baseConfig();
        return new DefaultKafkaProducerFactory<>(cfg, new StringSerializer(), new JsonSerializer<>(objectMapper));
    }

    @Bean
    public KafkaTemplate<String, DuplicateCheckKafkaEvent> kafkaTemplate() {
        return new KafkaTemplate<>(eventProducerFactory());
    }

    private Map<String, Object> baseConfig() {
        Map<String, Object> cfg = new HashMap<>();
        cfg.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        cfg.put(ProducerConfig.CLIENT_ID_CONFIG,          props.getClientId());
        cfg.put(ProducerConfig.ACKS_CONFIG,               props.getAcks());
        cfg.put(ProducerConfig.RETRIES_CONFIG,            props.getRetries());
        cfg.put(ProducerConfig.MAX_BLOCK_MS_CONFIG,       props.getMaxBlockTimeInMs());
        cfg.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
                props.getMaxInFlightRequestsPerCon());
        cfg.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, props.getRequestTimeoutInMs());
        cfg.put(ProducerConfig.LINGER_MS_CONFIG,          props.getLingerMs());
        cfg.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG,props.getDeliveryTimeoutInMs());
        return cfg;
    }
}
