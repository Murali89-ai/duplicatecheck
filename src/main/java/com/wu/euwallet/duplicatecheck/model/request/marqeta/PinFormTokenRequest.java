package com.wu.euwallet.duplicatecheck.model.request.marqeta;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PinFormTokenRequest {
    private String cardToken;
    private String userToken;
    private String deviceType;
}
