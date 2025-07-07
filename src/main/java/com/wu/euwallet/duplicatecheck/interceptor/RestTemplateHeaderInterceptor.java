package com.wu.euwallet.duplicatecheck.interceptor;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestTemplateHeaderInterceptor implements ClientHttpRequestInterceptor {

    @Override
    @NonNull
    public ClientHttpResponse intercept(
            @NonNull HttpRequest request,
            @NonNull byte[] body,
            @NonNull ClientHttpRequestExecution execution) throws IOException {

        // Add custom headers here
        request.getHeaders().add("X-Correlation-ID", "your-correlation-id");
        request.getHeaders().add("X-Source-System", "duplicate-check");

        return execution.execute(request, body);
    }
}
