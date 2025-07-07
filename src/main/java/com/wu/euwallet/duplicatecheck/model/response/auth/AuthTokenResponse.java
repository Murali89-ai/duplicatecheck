package com.wu.euwallet.duplicatecheck.model.response.auth;

import lombok.Data;

@Data
public class AuthTokenResponse {
    private String access_token;
    private String token_type;
    private long expires_in;
    public String getAccessToken() { return access_token; }
}