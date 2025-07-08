package com.wu.euwallet.duplicatecheck.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "integration.ucd")
public class UcdConfig {
    private String baseUrl;          // e.g. https://ucd-stage.westernunion.com
    private String lookupPath;       // e.g. /api/v1/customer/lookup
    private String updatePath;       // e.g. /api/v1/customer/update
    private String appName;
    private String appVersion;
    private String requestedBy;      // maps to p('ucd.requestedBy')
}
