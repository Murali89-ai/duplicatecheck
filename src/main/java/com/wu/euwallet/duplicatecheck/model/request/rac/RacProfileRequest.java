package com.wu.euwallet.duplicatecheck.model.request.rac;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RacProfileRequest {
    private String customerNumber;
    private String emailId;
    private String mobileNumber;
    private String firstName;
    private String lastName;
    private String language;
    private String channel;
}
