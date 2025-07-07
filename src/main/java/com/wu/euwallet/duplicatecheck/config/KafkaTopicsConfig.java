package com.wu.euwallet.duplicatecheck.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicsConfig {
    private final KafkaTopicsProperties kafkaTopicsProperties;

    public String getBusinessTopic() {
        return kafkaTopicsProperties.getBusinessTopic();
    }

    public String getErrorTopic() {
        return kafkaTopicsProperties.getErrorTopic();
    }
}
