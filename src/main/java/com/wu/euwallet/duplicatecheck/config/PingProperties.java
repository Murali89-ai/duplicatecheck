package com.wu.euwallet.duplicatecheck.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ping")
@Data
public class PingProperties {
    private String baseUrl;
    private String updatePath;
}
