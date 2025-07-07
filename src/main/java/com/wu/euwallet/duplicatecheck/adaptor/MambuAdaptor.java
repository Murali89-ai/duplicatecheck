package com.wu.euwallet.duplicatecheck.adaptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.euwallet.duplicatecheck.aop.LoggingAnnotation;
import com.wu.euwallet.duplicatecheck.config.MambuConfig;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUExceptionType;
import com.wu.euwallet.duplicatecheck.exception.utils.WUServiceExceptionUtils;
import com.wu.euwallet.duplicatecheck.model.request.ProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
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
public class MambuAdaptor {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final MambuConfig mambuConfig;

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
    public JsonNode updateCustomer(ProfileUpdateRequest request) {
        try {
            // Construct the target URL
            String customerId = request.getCustomerNumber();
            String url = mambuConfig.getBaseUrl() + mambuConfig.getCustomerPath().replace("{customerId}", customerId);

            // Build request body from incoming request
            JsonNode requestBody = mapper.convertValue(request, JsonNode.class);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", mambuConfig.getAuthorization());

            HttpEntity<JsonNode> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.PATCH,
                    entity,
                    JsonNode.class
            );

            log.info("Mambu customer updated for customerId={} – response={}", customerId, response.getBody());
            return response.getBody();

        } catch (Exception ex) {
            log.error("Mambu update failed", ex);
            throw WUServiceExceptionUtils.buildWUServiceException(WUExceptionType.MAMBU_ERROR, ex.getMessage());
        }
    }
}
