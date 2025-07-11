package com.wu.euwallet.duplicatecheck.model.request.biz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the request body sent to Biz service to change the card PIN.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizChangePinRequest {

    private String cardId;
    private String customerId;
    private String oldPin;
    private String newPin;
    private String channel;
    private String requestTime;
    private String requestId;
    private String customerNumber;
    private String transactionId;
}
