// model/request/biz/PinChangeRequest.java
package com.wu.euwallet.duplicatecheck.model.request.biz;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PinChangeRequest {
    private String cardNumber;
    private String newPin;
}
