package com.wu.euwallet.duplicatecheck.adaptor;

import com.wu.euwallet.duplicatecheck.aop.LoggingAnnotation;
import com.wu.euwallet.duplicatecheck.config.PingConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.net.SocketTimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PingAdaptor {

    private final RestTemplate restTemplate;
    private final PingConfig cfg;

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
    public boolean validateDevice(String jwtToken) {
        HttpHeaders h = new HttpHeaders();
        h.set("x-device-jwt", jwtToken);
        try {
            restTemplate.exchange(cfg.getValidateUrl(),
                    HttpMethod.GET, new HttpEntity<>(h), Void.class);
            return true;
        } catch (Exception ex) {
            log.warn("Ping validation failed: {}", ex.getMessage());
            return false;
        }
    }
}
