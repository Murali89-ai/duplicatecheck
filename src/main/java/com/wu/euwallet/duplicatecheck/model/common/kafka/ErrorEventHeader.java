package com.wu.euwallet.duplicatecheck.model.common.kafka;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorEventHeader {
    private String eventName;
    private String eventType;
    private String eventVersion;
    private String eventTime;
    private String errorCode;
    private String errorMessage;
    private String trackingId;
}
