package com.wu.euwallet.duplicatecheck.adaptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.euwallet.duplicatecheck.aop.LoggingAnnotation;
import com.wu.euwallet.duplicatecheck.config.BizConfig;
import com.wu.euwallet.duplicatecheck.constants.AppConstants;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUExceptionType;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUServiceException;
import com.wu.euwallet.duplicatecheck.exception.utils.WUServiceExceptionUtils;
import com.wu.euwallet.duplicatecheck.model.common.kafka.TransactionData;
import com.wu.euwallet.duplicatecheck.model.request.biz.BizChangePinRequest;
import com.wu.euwallet.duplicatecheck.model.response.biz.BizGetCardDetailsResponse;
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
public class Biz {

    private static final Logger logger = LogManager.getLogger(Biz.class);

    private final BizConfig bizProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AuthTokenProvider authTokenProvider;

    @LoggingAnnotation
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2), retryFor = {WUServiceException.class})
    public BizGetCardDetailsResponse getCardDetails(String cardNumber, TransactionData transactionData) {
        try {
            String url = bizProperties.getBaseUrl() + bizProperties.getCardDetailsPath();
            logger.info("Calling Biz to get card details: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(AppConstants.AUTHORIZATION, authTokenProvider.getAccessToken());
            headers.set(AppConstants.TRANSACTION_ID, transactionData.getTransactionId());

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<BizGetCardDetailsResponse> response = restTemplate.exchange(url + "/" + cardNumber, HttpMethod.GET, requestEntity, BizGetCardDetailsResponse.class);

            return response.getBody();
        } catch (Exception ex) {
            logger.error("Error while fetching card details from Biz", ex);
            throw WUServiceExceptionUtils.buildWUServiceException(WUExceptionType.BIZ_CARD_DETAILS_FAILED, "Biz getCardDetails failed", ex);
        }
    }

    @LoggingAnnotation
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2), retryFor = {WUServiceException.class})
    public void bizChangePin(BizChangePinRequest request, TransactionData transactionData) {
        try {
            String url = bizProperties.getBaseUrl() + bizProperties.getChangePinPath();
            logger.info("Calling Biz Change PIN: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(AppConstants.AUTHORIZATION, authTokenProvider.getAccessToken());
            headers.set(AppConstants.TRANSACTION_ID, transactionData.getTransactionId());

            HttpEntity<String> requestEntity = new HttpEntity<>(objectMapper.writeValueAsString(request), headers);

            restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        } catch (Exception ex) {
            logger.error("Error while performing Biz PIN change", ex);
            throw WUServiceExceptionUtils.buildWUServiceException(WUExceptionType.BIZ_CHANGE_PIN_FAILED, "Biz Change PIN failed", ex);
        }
    }
}
