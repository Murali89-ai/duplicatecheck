package com.wu.euwallet.duplicatecheck.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "integration.marqeta")
public class MarqetaConfig {
    private String baseUrl;
    private String cardUpdatePath;       // e.g., /cards/{token}
    private String pinFormTokenPath;     // e.g., /pins/formtoken
}
