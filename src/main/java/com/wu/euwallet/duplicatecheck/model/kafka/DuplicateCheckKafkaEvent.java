package com.wu.euwallet.duplicatecheck.model.kafka;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DuplicateCheckKafkaEvent {
    private String correlationId;
    private String externalRefId;
    private String status;
    private String message;
    private String sourceSystem;
    private String eventType;
    private String eventTime;
}
