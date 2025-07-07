package com.wu.euwallet.duplicatecheck.adaptor;

import com.wu.euwallet.duplicatecheck.aop.LoggingAnnotation;
import com.wu.euwallet.duplicatecheck.config.SfmcConfig;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUExceptionType;
import com.wu.euwallet.duplicatecheck.exception.utils.WUServiceExceptionUtils;
import com.wu.euwallet.duplicatecheck.model.request.sfmc.SfmcEventRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.net.SocketTimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SfmcAdaptor {

    private final RestTemplate restTemplate;
    private final SfmcConfig cfg;
    private final AuthTokenProvider tokenProvider;

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
    public void pushEvent(SfmcEventRequest req) {
        try {
            HttpHeaders h = new HttpHeaders();
            h.setContentType(MediaType.APPLICATION_JSON);
            h.setBearerAuth(tokenProvider.bearer().substring(7));

            restTemplate.postForEntity(cfg.getEventsUrl(),
                    new HttpEntity<>(req, h), Void.class);
        } catch (Exception ex) {
            throw  WUServiceExceptionUtils.buildWUServiceException(
                    WUExceptionType.SFMC_ERROR, "SFMC push failed", ex);
        }
    }
}
