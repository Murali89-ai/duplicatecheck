package com.wu.euwallet.duplicatecheck.model.response.mambu;

import lombok.Data;

@Data
public class MambuCustomerResponse {
    private String encodedKey;
    private String id;
    private String firstName;
    private String lastName;
    private String state;
}
