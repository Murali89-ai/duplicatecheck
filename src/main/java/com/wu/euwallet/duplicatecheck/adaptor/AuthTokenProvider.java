package com.wu.euwallet.duplicatecheck.adaptor;

import com.wu.euwallet.duplicatecheck.aop.LoggingAnnotation;
import com.wu.euwallet.duplicatecheck.config.AuthTokenConfig;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUExceptionType;
import com.wu.euwallet.duplicatecheck.exception.utils.WUServiceExceptionUtils;
import com.wu.euwallet.duplicatecheck.model.response.auth.AuthTokenResponse;
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
public class AuthTokenProvider {

    private final RestTemplate restTemplate;
    private final AuthTokenConfig cfg;

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
    public String bearer() {
        try {
            HttpHeaders h = new HttpHeaders();
            h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            h.setBasicAuth(cfg.getClientId(), cfg.getClientSecret());

            HttpEntity<String> entity =
                    new HttpEntity<>("grant_type=client_credentials", h);

            ResponseEntity<AuthTokenResponse> res = restTemplate.exchange(
                    cfg.getTokenUrl(), HttpMethod.POST, entity, AuthTokenResponse.class);

            assert res.getBody() != null;
            return "Bearer " + res.getBody().getAccessToken();
        } catch (Exception ex) {
            throw WUServiceExceptionUtils.buildWUServiceException(
                    WUExceptionType.AUTH_TOKEN_ERROR, "Auth token retrieval failed", ex);
        }
    }
}
