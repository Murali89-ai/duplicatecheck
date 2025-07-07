package com.wu.euwallet.duplicatecheck.adaptor;

import com.wu.euwallet.duplicatecheck.aop.LoggingAnnotation;
import com.wu.euwallet.duplicatecheck.config.RacConfig;          // create if needed
import com.wu.euwallet.duplicatecheck.model.request.rac.RacProfileRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
public class RacAdaptor {

    private final RestTemplate restTemplate;
    private final RacConfig cfg;

    @LoggingAnnotation
    @Retryable(maxAttempts = 3,
        backoff = @Backoff(delay = 500, multiplier = 2),
        retryFor = { ResourceAccessException.class, HttpServerErrorException.class,
                     HttpClientErrorException.class, RestClientException.class,
                     SocketTimeoutException.class })
    public void lookupProfile(RacProfileRequest req) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.postForEntity(cfg.getBaseUrl() + cfg.getLookupPath(),
                                   new HttpEntity<>(req, h), Void.class);
    }
}
