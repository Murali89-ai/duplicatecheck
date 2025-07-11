package com.wu.euwallet.duplicatecheck.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "integration.ucd")
public class UcdConfig {
    private String baseUrl;          // e.g. https://ucd-stage.westernunion.com
    private String lookupPath;       // e.g. /api/v1/customer/lookup
    private String updatePath;       // e.g. /api/v1/customer/update
    private String appName;
    private String appVersion;
    private String requestedBy;      // maps to p('ucd.requestedBy')
    private String apiKey;
    private String bearerToken;
    private String duplicateCheckPath;
}
