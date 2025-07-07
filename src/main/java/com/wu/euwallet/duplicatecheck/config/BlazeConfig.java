package com.wu.euwallet.duplicatecheck.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "blaze")
public class BlazeConfig {
    private String baseUrl;
    private String endpoint;
    private String apiKey;
}