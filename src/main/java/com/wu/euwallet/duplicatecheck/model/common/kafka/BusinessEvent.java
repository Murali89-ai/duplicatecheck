package com.wu.euwallet.duplicatecheck.model.common.kafka;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusinessEvent {
    private BusinessEventHeader header;
    private ProfileUpdatePayload payload;
}
