package com.wu.euwallet.duplicatecheck.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.euwallet.duplicatecheck.config.AuthTokenConfig;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUExceptionType;
import com.wu.euwallet.duplicatecheck.exception.utils.WUServiceExceptionUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenProvider {

    private final AuthTokenConfig authTokenConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private String cachedToken;
    private Instant expiryTime;

    @PostConstruct
    public void init() {
        this.cachedToken = null;
        this.expiryTime = Instant.EPOCH; // expired by default
    }

    public String getAccessToken() {
        if (cachedToken == null || Instant.now().isAfter(expiryTime)) {
            refreshToken();
        }
        return cachedToken;
    }

    private void refreshToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = getMultiValueMapHttpEntity(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    authTokenConfig.getTokenUrl(),
                    HttpMethod.POST,
                    request,
                    String.class
            );

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            this.cachedToken = jsonNode.get("access_token").asText();
            int expiresIn = jsonNode.get("expires_in").asInt();
            this.expiryTime = Instant.now().plusSeconds(expiresIn - 60); // Refresh 1 minute before expiry

            log.info("Successfully refreshed access token.");
        } catch (Exception e) {
            log.error("Failed to refresh access token", e);
            throw WUServiceExceptionUtils.buildWUServiceException(WUExceptionType.AUTH_TOKEN_REFRESH_FAILED, e.getMessage());
        }
    }

    private HttpEntity<MultiValueMap<String, String>> getMultiValueMapHttpEntity(HttpHeaders headers) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", authTokenConfig.getClientId());
        body.add("client_secret", authTokenConfig.getClientSecret());
        body.add("username", authTokenConfig.getTokenUsername());
        body.add("password", authTokenConfig.getTokenPassword());
        body.add("scope", authTokenConfig.getTokenScope());

        return new HttpEntity<>(body, headers);
    }
}