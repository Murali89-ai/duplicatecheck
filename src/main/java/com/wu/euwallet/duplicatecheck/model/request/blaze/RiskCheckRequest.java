package com.wu.euwallet.duplicatecheck.model.request.blaze;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RiskCheckRequest {
    private String customerNumber;
    private String channel;
    private String transactionId;
}