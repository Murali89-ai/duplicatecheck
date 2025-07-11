package com.wu.euwallet.duplicatecheck.model.response.biz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the response from Biz service for getCardDetails call.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizGetCardDetailsResponse {

    private String cardId;
    private String cardStatus;
    private String cardType;
    private String customerName;
    private String expiryDate;
    private String maskedCardNumber;
    private String issuer;
    private String pinStatus;
}
