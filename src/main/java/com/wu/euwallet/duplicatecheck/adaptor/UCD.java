package com.wu.euwallet.duplicatecheck.adaptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.euwallet.duplicatecheck.config.UcdConfig;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUExceptionType;
import com.wu.euwallet.duplicatecheck.exception.utils.WUServiceExceptionUtils;
import com.wu.euwallet.duplicatecheck.model.request.ucd.UcdRequest;
import com.wu.euwallet.duplicatecheck.provider.AuthTokenProvider;
import com.wu.euwallet.duplicatecheck.aop.LoggingAnnotation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class UCD {

    private static final Logger log = LoggerFactory.getLogger(UCD.class);

    private final UcdConfig ucdConfig;
    private final AuthTokenProvider authTokenProvider;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @LoggingAnnotation
    @Retryable(
            value = { Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public JsonNode lookupCustomer(JsonNode request, String correlationId) {
        try {
            String url = ucdConfig.getBaseUrl() + ucdConfig.getLookupPath();
            HttpEntity<JsonNode> entity = prepareRequest(request, correlationId);
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, entity, JsonNode.class);
            return response.getBody();
        } catch (Exception ex) {
            log.error("Error during UCD lookup: {}", ex.getMessage(), ex);
            throw WUServiceExceptionUtils.buildWUServiceException(WUExceptionType.UCD_LOOKUP_FAILED, ex.getMessage());
        }
    }

    @LoggingAnnotation
    @Retryable(
            value = { Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public JsonNode updateCustomer(JsonNode request, String correlationId) {
        try {
            String url = ucdConfig.getBaseUrl() + ucdConfig.getUpdatePath();
            HttpEntity<JsonNode> entity = prepareRequest(request, correlationId);
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, entity, JsonNode.class);
            return response.getBody();
        } catch (Exception ex) {
            log.error("Error during UCD update: {}", ex.getMessage(), ex);
            throw WUServiceExceptionUtils.buildWUServiceException(WUExceptionType.UCD_UPDATE_FAILED, ex.getMessage());
        }
    }

    @LoggingAnnotation
    @Retryable(
            value = { Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public JsonNode checkForDuplicate(UcdRequest request) {
        try {
            String url = ucdConfig.getBaseUrl() + ucdConfig.getDuplicateCheckPath();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("x-api-key", ucdConfig.getApiKey());
            headers.setBearerAuth(authTokenProvider.getAccessToken());

            HttpEntity<UcdRequest> entity = new HttpEntity<>(request, headers);
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, entity, JsonNode.class);

            return response.getBody();
        } catch (Exception ex) {
            log.error("Error during UCD duplicate check: {}", ex.getMessage(), ex);
            throw WUServiceExceptionUtils.buildWUServiceException(WUExceptionType.UCD_DUPLICATE_CHECK_FAILED);
        }
    }

    private HttpEntity<JsonNode> prepareRequest(JsonNode request, String correlationId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("x-api-key", ucdConfig.getApiKey());
        headers.set("x-wu-correlationId", correlationId);
        headers.setBearerAuth(authTokenProvider.getAccessToken());
        return new HttpEntity<>(request, headers);
    }
}
