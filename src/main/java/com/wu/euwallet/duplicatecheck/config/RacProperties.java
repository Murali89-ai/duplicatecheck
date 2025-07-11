package com.wu.euwallet.duplicatecheck.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rac")
public class RacProperties {

    private String baseUrl;
    private String updatePath;
    private String apiKey;
    private String correlationIdHeader;
}
