package com.wu.euwallet.duplicatecheck.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sfmc")
@Data
public class SFMCProperties {
    private String baseUrl;
    private String notificationPath;
    private String url;
    private String eventDefinitionKey;
    private String messageKeyPhone;
    private String messageKeyEmail;
}
