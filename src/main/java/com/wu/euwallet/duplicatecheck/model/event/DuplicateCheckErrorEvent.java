package com.wu.euwallet.duplicatecheck.model.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DuplicateCheckErrorEvent {

    private String errorCode;
    private String errorMessage;
    private String failurePoint;
    private Instant eventTimestamp;
}
