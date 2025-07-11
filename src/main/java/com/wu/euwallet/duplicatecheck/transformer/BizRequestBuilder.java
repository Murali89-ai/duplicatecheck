package com.wu.euwallet.duplicatecheck.transformer;

import com.wu.euwallet.duplicatecheck.model.request.ProfileUpdateRequest;
import com.wu.euwallet.duplicatecheck.model.request.biz.BizChangePinRequest;

public class BizRequestBuilder {
    public static BizChangePinRequest buildChangePinRequest(ProfileUpdateRequest request) {
        return BizChangePinRequest.builder()
            .customerNumber(request.getCustomerNumber())
            .newPin(request.getNewPin()) // assuming it exists
            .transactionId(request.getTransactionId()) // or similar fields
            .build();
    }
}
