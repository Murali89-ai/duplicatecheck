package com.wu.euwallet.duplicatecheck.adaptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.euwallet.duplicatecheck.aop.LoggingAnnotation;
import com.wu.euwallet.duplicatecheck.config.MarqetaProperties;
import com.wu.euwallet.duplicatecheck.constants.AppConstants;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUExceptionType;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUServiceException;
import com.wu.euwallet.duplicatecheck.exception.utils.WUServiceExceptionUtils;
import com.wu.euwallet.duplicatecheck.model.common.kafka.TransactionData;
import com.wu.euwallet.duplicatecheck.model.request.ProfileUpdateRequest;
import com.wu.euwallet.duplicatecheck.provider.AuthTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class Marqeta {

    private static final Logger logger = LogManager.getLogger(Marqeta.class);

    private final MarqetaProperties marqetaProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AuthTokenProvider authTokenProvider;

    @LoggingAnnotation
    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = {WUServiceException.class}
    )
    public void updateCustomer(ProfileUpdateRequest request, TransactionData transactionData) {
        try {
            String url = marqetaProperties.getBaseUrl() + marqetaProperties.getUpdatePath();
            logger.info("Calling Marqeta to update customer: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(AppConstants.AUTHORIZATION, authTokenProvider.getAccessToken());
            headers.set(AppConstants.TRANSACTION_ID, transactionData.getTransactionId());

            HttpEntity<String> requestEntity = new HttpEntity<>(
                    objectMapper.writeValueAsString(request),
                    headers
            );

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            logger.info("Marqeta update successful, status: {}", response.getStatusCode());

        } catch (Exception ex) {
            logger.error("Marqeta update failed", ex);
            throw WUServiceExceptionUtils.buildWUServiceException(
                    WUExceptionType.MARQETA_UPDATE_FAILED,
                    "Marqeta customer update failed",
                    ex
            );
        }
    }
    @LoggingAnnotation
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000), retryFor = { Exception.class })
    public void retry(JsonNode dlqPayload) {
        try {
            logger.info("Retrying Marqeta event: {}", dlqPayload);
            // Example: POST to retry endpoint
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(dlqPayload.toString(), headers);

            restTemplate.postForEntity(marqetaProperties.getRetryUrl(), request, String.class);
        } catch (Exception e) {
            log.error("Retry failed for Marqeta DLQ payload", e);
            throw WUServiceExceptionUtils.buildWUServiceException(WUExceptionType.MARQETA_UPDATE_FAILED,"MARQETA_UPDATE_FAILED");
        }
    }

}
