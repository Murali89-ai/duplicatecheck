package com.wu.euwallet.duplicatecheck.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthTokenConfig {
    private String tokenUrl;
    private String clientId;
    private String clientSecret;
    private String tokenUcdXApiKey;
    private String tokenUsername;
    private String tokenScope;
    private String tokenPassword;
    private String jwtInstance;
}