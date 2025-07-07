package com.wu.euwallet.duplicatecheck.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    @NonNull
    public ClientHttpResponse intercept(@NonNull HttpRequest request,
                                        @NonNull byte[] body,
                                        @NonNull ClientHttpRequestExecution execution) throws IOException {

        log.info("➡️ [Request] URI       : {}", request.getURI());
        log.info("➡️ [Request] Method    : {}", request.getMethod());
        log.info("➡️ [Request] Headers   : {}", request.getHeaders());
        log.info("➡️ [Request] Body      : {}", new String(body, StandardCharsets.UTF_8));

        ClientHttpResponse response = execution.execute(request, body);

        String responseBody = new BufferedReader(
                new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));

        log.info("⬅️ [Response] Status   : {}", response.getStatusCode());
        log.info("⬅️ [Response] Headers  : {}", response.getHeaders());
        log.info("⬅️ [Response] Body     : {}", responseBody);

        return response;
    }
}
