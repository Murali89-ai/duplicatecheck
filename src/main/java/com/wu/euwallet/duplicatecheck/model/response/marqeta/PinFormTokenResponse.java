package com.wu.euwallet.duplicatecheck.model.response.marqeta;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PinFormTokenResponse {

    @JsonProperty("token")
    private String token;

    @JsonProperty("expires")
    private String expires;

    @JsonProperty("status")
    private String status;
}
