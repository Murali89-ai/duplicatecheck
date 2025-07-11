package com.wu.euwallet.duplicatecheck.adaptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.euwallet.duplicatecheck.aop.LoggingAnnotation;
import com.wu.euwallet.duplicatecheck.config.SFMCProperties;
import com.wu.euwallet.duplicatecheck.constants.AppConstants;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUExceptionType;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUServiceException;
import com.wu.euwallet.duplicatecheck.exception.utils.WUServiceExceptionUtils;
import com.wu.euwallet.duplicatecheck.model.common.kafka.TransactionData;
import com.wu.euwallet.duplicatecheck.model.request.ProfileUpdateRequest;
import com.wu.euwallet.duplicatecheck.provider.AuthTokenProvider;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class SFMC {

    private static final Logger logger = LogManager.getLogger(SFMC.class);

    private final SFMCProperties sfmcProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AuthTokenProvider authTokenProvider;

    @LoggingAnnotation
    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = {WUServiceException.class}
    )
    public void sendCommunication(ProfileUpdateRequest request, TransactionData transactionData) {
        try {
            String url = sfmcProperties.getBaseUrl() + sfmcProperties.getNotificationPath();
            logger.info("Calling SFMC to send communication: {}", url);

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

            logger.info("SFMC response: {}", response.getStatusCode());

        } catch (Exception ex) {
            logger.error("Error while sending communication to SFMC", ex);
            throw WUServiceExceptionUtils.buildWUServiceException(
                    WUExceptionType.SFMC_COMMUNICATION_FAILED,
                    "SFMC communication failed",
                    ex
            );
        }
    }
}
