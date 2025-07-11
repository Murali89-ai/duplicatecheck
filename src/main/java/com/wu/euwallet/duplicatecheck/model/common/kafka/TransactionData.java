package com.wu.euwallet.duplicatecheck.model.common.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents metadata related to a transaction, typically used for logging,
 * Kafka events, and audit trails across services.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionData {

    private String transactionId;
    private String transactionDateTime;
    private String applicationId;
    private String correlationId;
    private String channel;
    private String deviceId;
    private String locale;
}
