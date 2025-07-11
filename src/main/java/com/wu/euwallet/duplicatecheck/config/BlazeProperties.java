package com.wu.euwallet.duplicatecheck.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "blaze")
@Data
public class BlazeProperties {
    private String baseUrl;
    private String rulesPath;
}
