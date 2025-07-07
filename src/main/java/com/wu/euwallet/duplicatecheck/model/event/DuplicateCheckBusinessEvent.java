package com.wu.euwallet.duplicatecheck.model.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DuplicateCheckBusinessEvent {
    private String eventName;
    private String eventType;
    private String eventSource;
    private String customerId;
    private String status;
    private String message;
    private Instant eventTimestamp;
}
