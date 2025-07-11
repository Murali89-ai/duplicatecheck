package com.wu.euwallet.duplicatecheck.model.request.mambu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MambuUpdateRequest {
    private String customerId;
    private String reason;
    private String eventId;
    private String timestamp;
}
