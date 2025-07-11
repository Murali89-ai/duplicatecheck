package com.wu.euwallet.duplicatecheck.adaptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.euwallet.duplicatecheck.aop.LoggingAnnotation;
import com.wu.euwallet.duplicatecheck.config.MambuProperties;
import com.wu.euwallet.duplicatecheck.constants.AppConstants;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUExceptionType;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUServiceException;
import com.wu.euwallet.duplicatecheck.exception.utils.WUServiceExceptionUtils;
import com.wu.euwallet.duplicatecheck.model.common.kafka.TransactionData;
import com.wu.euwallet.duplicatecheck.model.request.mambu.MambuUpdateRequest;
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
public class Mambu {

    private static final Logger logger = LogManager.getLogger(Mambu.class);

    private final MambuProperties mambuProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AuthTokenProvider authTokenProvider;

    @LoggingAnnotation
    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = {WUServiceException.class}
    )
    public void notifyMambu(MambuUpdateRequest request, TransactionData transactionData) {
        try {
            String url = mambuProperties.getBaseUrl() + mambuProperties.getUpdatePath();
            logger.info("Calling Mambu DLQ notification API: {}", url);

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

            logger.info("Mambu DLQ update response: {}", response.getStatusCode());

        } catch (Exception ex) {
            logger.error("Mambu DLQ update failed", ex);
            throw WUServiceExceptionUtils.buildWUServiceException(
                    WUExceptionType.MAMBU_DLQ_UPDATE_FAILED,
                    "Failed to notify Mambu",
                    ex
            );
        }
    }

    @LoggingAnnotation
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000), retryFor = { Exception.class })
    public void retry(JsonNode dlqPayload) {
        try {
            log.info("Retrying Mambu event: {}", dlqPayload);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(dlqPayload.toString(), headers);

            restTemplate.postForEntity(mambuProperties.getRetryUrl(), request, String.class);
        } catch (Exception e) {
            log.error("Retry failed for Mambu DLQ payload", e);
            throw WUServiceExceptionUtils.buildWUServiceException(WUExceptionType.MAMBU_UPDATE_FAILED,"MAMBU_UPDATE_FAILED");
        }
    }

}
