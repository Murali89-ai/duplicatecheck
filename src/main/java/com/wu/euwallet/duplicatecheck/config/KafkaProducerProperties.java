package com.wu.euwallet.duplicatecheck.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "kafka.producer")
public class KafkaProducerProperties {

    private String bootstrapServers;
    private String clientId;
    private String acks;
    private Integer retries;
    private Integer maxBlockTimeInMs;
    private Integer maxInFlightRequestsPerCon;
    private Integer requestTimeoutInMs;
    private Integer lingerMs;
    private Integer deliveryTimeoutInMs;
}
