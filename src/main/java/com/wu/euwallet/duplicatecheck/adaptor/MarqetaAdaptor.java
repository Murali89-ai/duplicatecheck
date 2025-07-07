package com.wu.euwallet.duplicatecheck.adaptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.wu.euwallet.duplicatecheck.aop.LoggingAnnotation;
import com.wu.euwallet.duplicatecheck.config.MarqetaConfig;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUExceptionType;
import com.wu.euwallet.duplicatecheck.exception.utils.WUServiceExceptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.net.SocketTimeoutException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarqetaAdaptor {

    private final RestTemplate restTemplate;
    private final MarqetaConfig cfg;

    // ───────────────────────────────────────────────────────────────
    // Update card status (PUT /cards/{token})
    // ───────────────────────────────────────────────────────────────
    @LoggingAnnotation
    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 2),
            retryFor = {
                    ResourceAccessException.class,
                    HttpServerErrorException.class,
                    HttpClientErrorException.class,
                    RestClientException.class,
                    SocketTimeoutException.class
            }
    )
    public JsonNode updateCard(String cardToken, JsonNode updatePayload) {
        try {
            String url = cfg.getBaseUrl() + cfg.getCardUpdatePath().replace("{token}", cardToken);
            HttpEntity<JsonNode> entity = new HttpEntity<>(updatePayload, buildHeaders());
            ResponseEntity<JsonNode> response =
                    restTemplate.exchange(url, HttpMethod.PUT, entity, JsonNode.class);

            log.info("Marqeta card update OK for token {}", cardToken);
            return response.getBody();
        } catch (Exception ex) {
            throw WUServiceExceptionUtils.buildWUServiceException(
                    WUExceptionType.MARQETA_ERROR, "Marqeta card update failed", ex);
        }
    }

    // ───────────────────────────────────────────────────────────────
    // Generate PIN form-token (POST /pins/formtoken)
    // ───────────────────────────────────────────────────────────────
    @LoggingAnnotation
    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 2),
            retryFor = {
                    ResourceAccessException.class,
                    HttpServerErrorException.class,
                    HttpClientErrorException.class,
                    RestClientException.class,
                    SocketTimeoutException.class
            }
    )
    public JsonNode generatePinFormToken(JsonNode payload) {
        try {
            String url = cfg.getBaseUrl() + cfg.getPinFormTokenPath();
            HttpEntity<JsonNode> entity = new HttpEntity<>(payload, buildHeaders());
            ResponseEntity<JsonNode> response =
                    restTemplate.postForEntity(url, entity, JsonNode.class);

            log.info("Marqeta PIN form-token generated");
            return response.getBody();
        } catch (Exception ex) {
            throw WUServiceExceptionUtils.buildWUServiceException(
                    WUExceptionType.MARQETA_ERROR, "Marqeta PIN form token failed", ex);
        }
    }

    // ───────────────────────────────────────────────────────────────
    private HttpHeaders buildHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.setAccept(List.of(MediaType.APPLICATION_JSON));
        return h;
    }
}
