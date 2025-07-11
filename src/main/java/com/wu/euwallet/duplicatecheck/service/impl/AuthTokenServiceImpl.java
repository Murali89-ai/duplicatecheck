package com.wu.euwallet.duplicatecheck.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.wu.euwallet.duplicatecheck.config.AuthTokenConfig;
import com.wu.euwallet.duplicatecheck.service.AuthTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthTokenServiceImpl implements AuthTokenService {

    private final AuthTokenConfig authTokenConfig;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate;

    private static final String JWT_TOKEN_KEY = "jwt_token";

    @Override
    public String refreshToken(boolean forceRefresh) {
        if (!forceRefresh) {
            Object cachedToken = redisTemplate.opsForValue().get(JWT_TOKEN_KEY);
            if (cachedToken != null) {
                log.info("Using cached JWT token from Redis.");
                return cachedToken.toString();
            }
        }

        log.info("Requesting new JWT token from {}", authTokenConfig.getTokenUrl());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");
            body.add("client_id", authTokenConfig.getClientId());
            body.add("client_secret", authTokenConfig.getClientSecret());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                    authTokenConfig.getTokenUrl(), request, JsonNode.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String token = response.getBody().get("access_token").asText();

                // Cache with 44-minute TTL
                redisTemplate.opsForValue().set(JWT_TOKEN_KEY, token, Duration.ofMinutes(44));

                log.info("Successfully fetched and cached JWT token.");
                return token;
            } else {
                throw new RuntimeException("Unexpected response from token server: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Failed to fetch JWT token from {}", authTokenConfig.getTokenUrl(), e);
            throw new RuntimeException("Failed to get JWT token", e);
        }
    }
}
