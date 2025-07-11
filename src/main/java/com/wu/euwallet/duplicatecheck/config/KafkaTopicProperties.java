package com.wu.euwallet.duplicatecheck.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kafka.topic")
public class KafkaTopicProperties {
    private String profileUpdateSuccess;
    private String profileUpdateDuplicate;
    private String marqetaFailure;
    private String bizFailure;
}
