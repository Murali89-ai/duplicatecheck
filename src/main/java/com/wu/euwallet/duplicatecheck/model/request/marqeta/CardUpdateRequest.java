// model/request/marqeta/CardUpdateRequest.java
package com.wu.euwallet.duplicatecheck.model.request.marqeta;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardUpdateRequest {
    private String cardToken;
    private String status;   // e.g. "ACTIVE"
}
