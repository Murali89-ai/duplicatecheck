package com.wu.euwallet.duplicatecheck.service;

import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.databind.JsonNode;

public interface MarqetaUpdateService {
    JsonNode process(@NotBlank String rawJson);
}


