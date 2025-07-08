package com.wu.euwallet.duplicatecheck.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.wu.euwallet.duplicatecheck.exception.exceptiontype.WUExceptionType;
import com.wu.euwallet.duplicatecheck.exception.utils.WUServiceExceptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HttpService {

    private final RestTemplate restTemplate;

    public <T> T postForObject(String url, Map<String, Object> payload, Class<T> responseType) {
        log.info("Calling POST on URL: {}", url);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);
            return response.getBody();
        } catch (Exception ex) {
            log.error("Exception while making POST request to {}: {}", url, ex.getMessage(), ex);
            throw ex; // or wrap and throw a custom exception like WUServiceException
        }
    }

    public JsonNode postForJson(String url, Map<String, Object> payload) {
        return postForObject(url, payload, JsonNode.class);
    }

    public JsonNode callUcdPatchEndpoint(String ucdPayload) {
        try {
            String url = "http://ucd-service/update";
            log.info("Calling UCD PATCH endpoint: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(ucdPayload, headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url, HttpMethod.PATCH, entity, JsonNode.class
            );

            return response.getBody();
        } catch (Exception ex) {
            log.error("Error while calling UCD PATCH endpoint", ex);
            throw WUServiceExceptionUtils.buildWUServiceException(
                    WUExceptionType.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

}