package com.wu.euwallet.duplicatecheck.adaptor;

import com.wu.euwallet.duplicatecheck.aop.LoggingAnnotation;
import com.wu.euwallet.duplicatecheck.config.BizConfig;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUExceptionType;
import com.wu.euwallet.duplicatecheck.exception.utils.WUServiceExceptionUtils;
import com.wu.euwallet.duplicatecheck.model.request.biz.PinChangeRequest;
import com.wu.euwallet.duplicatecheck.model.response.biz.BizCardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class BizAdaptor {

    private final RestTemplate restTemplate;
    private final BizConfig cfg;
    private final AuthTokenProvider tokenProvider;

    private HttpHeaders bearer() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(tokenProvider.bearer().substring(7));
        return h;
    }

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
    public BizCardResponse getCardDetails(String cardNumber) {
        try {
            String url = cfg.getBaseUrl() + cfg.getCardDetailsPath().formatted(cardNumber);
            return restTemplate.exchange(url, HttpMethod.GET,
                    new HttpEntity<>(bearer()), BizCardResponse.class).getBody();
        } catch (Exception ex) {
            throw WUServiceExceptionUtils.buildWUServiceException(
                    WUExceptionType.BIZ_ERROR, "Biz getCardDetails failed", ex);
        }
    }

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
    public void changePin(PinChangeRequest req) {
        try {
            restTemplate.exchange(cfg.getBaseUrl() + cfg.getChangePinPath(),
                    HttpMethod.POST,
                    new HttpEntity<>(req, bearer()), Void.class);
        } catch (Exception ex) {
            throw WUServiceExceptionUtils.buildWUServiceException(
                    WUExceptionType.BIZ_ERROR, "Biz changePin failed", ex);
        }
    }
}
