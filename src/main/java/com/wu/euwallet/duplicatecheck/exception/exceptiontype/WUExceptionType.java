package com.wu.euwallet.duplicatecheck.exception.exceptiontype;

import lombok.Getter;

@Getter
public enum WUExceptionType {
    DLQ_KAFKA_ERROR("DLQ_KAFKA_ERROR",""),
    DLQ_SERIALIZATION_FAILED("DLQ_SERIALIZATION_FAILED","dlqSerializationFailed"),
    INTERNAL_ERROR("INTERNAL_ERROR", "Internal server error"),
    REQUEST_VALIDATION_EXCEPTION("E1001", "Request validation failed"),
    DATA_NOT_FOUND("E1002", "Requested data not found"),
    UNAUTHORIZED("E1003", "Unauthorized access"),
    INTERNAL_SERVER_ERROR("E1004", "Internal server error"),
    EXTERNAL_SERVICE_FAILURE("E1005", "External service failure"),
    DUPLICATE_RECORD("E1006", "Duplicate record detected"),
    BLAZE_RISK_REJECTED("E1007","Blaze rejected the transaction"),
    MARQETA_ERROR("E1008","Marqeta integration error"),
    BIZ_ERROR("E1009","Biz integration error"),
    MAMBU_ERROR("E1010","Mambu integration error"),
    PING_ERROR("E1011","Ping validation error"),
    SFMC_ERROR("E1012","SFMC integration error"),
    AUTH_TOKEN_ERROR("E1013","Auth token retrieval error"),
    BLAZE_ERROR("E1007", "Blaze risk check failed"),
    RAC_ERROR("E1014", "RAC profile lookup error");


    private final String code;
    private final String message;

    WUExceptionType(String code, String message) {
        this.code = code;
        this.message = message;
    }

}