package com.wu.euwallet.duplicatecheck.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for sending response back to the client for duplicate check requests.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DuplicateCheckResponse {

    private String status;
    private String message;
    private String duplicateFlag;
    private String reasonCode;
    private String errorCode;
    private String errorMessage;
    private String customerUmn;
    private String transactionId;
    private String description;
    private String correlationId;
}
