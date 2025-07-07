package com.wu.euwallet.duplicatecheck.model.common.kafka;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileUpdatePayload {
    private String customerNumber;
    private String mobileNumber;
    private String emailId;
    private String firstName;
    private String lastName;
    private String language;
    private String channel;
    private String transactionId;
    private String deviceId;
    private String updatedTime;
    private String eventType;
    private String sourceSystem;

}
