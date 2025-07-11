package com.wu.euwallet.duplicatecheck.model.request.ucd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UcdRequest {
    private String partyId;
    private String cardId;
    private String channel;
    private String transactionId;
    private String requestTime;
}
