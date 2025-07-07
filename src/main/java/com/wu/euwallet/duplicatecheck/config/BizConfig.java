package com.wu.euwallet.duplicatecheck.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "integration.biz")
public class BizConfig {
    private String baseUrl;
    private String cardDetailsPath;
    private String changePinPath;
}
