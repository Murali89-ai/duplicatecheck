package com.wu.euwallet.duplicatecheck.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "marqeta")
@Data
public class MarqetaProperties {
    private String baseUrl;
    private String updatePath;
    private String retryUrl;

}
