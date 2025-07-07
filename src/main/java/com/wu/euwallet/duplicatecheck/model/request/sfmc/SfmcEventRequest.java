package com.wu.euwallet.duplicatecheck.model.request.sfmc;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class SfmcEventRequest {
    private String contactKey;
    private String eventDefinitionKey;
    private Map<String, Object> data;
}
