package com.wu.euwallet.duplicatecheck.model.response.blaze;

import lombok.Data;

@Data
public class RiskCheckResponse {
    private String status;
    private String decision;
    private String reason;
}