package com.wu.euwallet.duplicatecheck.adaptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.euwallet.duplicatecheck.aop.LoggingAnnotation;
import com.wu.euwallet.duplicatecheck.config.BlazeConfig;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUExceptionType;
import com.wu.euwallet.duplicatecheck.exception.utils.WUServiceExceptionUtils;
import com.wu.euwallet.duplicatecheck.model.request.blaze.RiskCheckRequest;
import com.wu.euwallet.duplicatecheck.model.response.blaze.RiskCheckResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class BlazeAdaptor {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final BlazeConfig cfg;

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
    public RiskCheckResponse performRiskCheck(RiskCheckRequest request) {
        try {
            String url = cfg.getBaseUrl() + cfg.getEndpoint();
            HttpHeaders h = new HttpHeaders();
            h.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(
                    objectMapper.writeValueAsString(request), h);

            ResponseEntity<RiskCheckResponse> res = restTemplate.exchange(
                    url, HttpMethod.POST, entity, RiskCheckResponse.class);
            return res.getBody();
        } catch (Exception ex) {
            throw WUServiceExceptionUtils.buildWUServiceException(
                    WUExceptionType.BLAZE_ERROR, "Blaze integration failed", ex);
        }
    }
}
