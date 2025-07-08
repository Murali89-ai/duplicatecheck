package com.wu.euwallet.duplicatecheck.adaptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.euwallet.duplicatecheck.config.UcdConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Slf4j
@Component
@RequiredArgsConstructor
public class UcdAdaptor {

    private final RestTemplate restTemplate;          // inject the one with interceptors
    private final UcdConfig ucdCfg;
    private final ObjectMapper mapper;

    @Retryable(maxAttempts = 3, include = Exception.class)
    public JsonNode lookupCustomer(JsonNode lookupBody) {
        String url = ucdCfg.getBaseUrl() + ucdCfg.getLookupPath();
        log.info("▶️  UCD Lookup POST {}", url);
        ResponseEntity<JsonNode> resp = restTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity<>(lookupBody), JsonNode.class);
        log.info("✅  UCD Lookup OK → {}", resp.getStatusCode());
        return resp.getBody();
    }

    @Retryable(maxAttempts = 3, include = Exception.class)
    public JsonNode updateCustomer(JsonNode updateBody) {
        String url = ucdCfg.getBaseUrl() + ucdCfg.getUpdatePath();
        log.info("▶️  UCD Update POST {}", url);
        ResponseEntity<JsonNode> resp = restTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity<>(updateBody), JsonNode.class);
        log.info("✅  UCD Update OK → {}", resp.getStatusCode());
        return resp.getBody();
    }
}
