package com.wu.euwallet.duplicatecheck.controller;

import com.wu.euwallet.duplicatecheck.dto.DuplicateCheckResponse;
import com.wu.euwallet.duplicatecheck.model.common.kafka.TransactionData;
import com.wu.euwallet.duplicatecheck.model.request.ProfileUpdateRequest;
import com.wu.euwallet.duplicatecheck.service.DuplicateCheckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.wu.euwallet.duplicatecheck.constants.AppConstants.*;

@RestController
@RequestMapping("app/v2/dupcheck")
@RequiredArgsConstructor
public class DuplicateCheckController {

    private static final Logger logger = LogManager.getLogger(DuplicateCheckController.class);

    private final DuplicateCheckService duplicateCheckService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DuplicateCheckResponse> updateProfile(
            @RequestHeader(value = TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(value = APPLICATION_ID, required = false) String applicationId,
            @RequestHeader(value = CORRELATION_ID, required = false) String correlationId,
            @RequestHeader(value = CHANNEL, required = false) String channel,
            @RequestHeader(value = DEVICE_ID, required = false) String deviceId,
            @RequestHeader(value = LOCALE, required = false) String locale,
            @Valid @RequestBody ProfileUpdateRequest profileUpdateRequest) {

        TransactionData transactionData = TransactionData.builder()
                .transactionId(transactionId)
                .applicationId(applicationId)
                .correlationId(correlationId)
                .channel(channel)
                .deviceId(deviceId)
                .locale(locale)
                .transactionDateTime(java.time.LocalDateTime.now().toString())
                .build();

        logger.info("Received profile update request for partyId: {}", profileUpdateRequest.getPartyId());

        DuplicateCheckResponse response = duplicateCheckService
                .processProfileUpdate(profileUpdateRequest, transactionData);

        return ResponseEntity.ok(response);
    }
}
