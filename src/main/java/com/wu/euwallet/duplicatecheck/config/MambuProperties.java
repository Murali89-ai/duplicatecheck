package com.wu.euwallet.duplicatecheck.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mambu")
@Getter
@Setter
public class MambuProperties {
    private String baseUrl;
    private String updatePath;
    private String retryUrl;

}
