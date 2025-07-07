package com.wu.euwallet.duplicatecheck.controller;

import com.wu.euwallet.duplicatecheck.aop.LoggingAnnotation;
import com.wu.euwallet.duplicatecheck.model.request.ProfileUpdateRequest;
import com.wu.euwallet.duplicatecheck.model.response.ProfileUpdateResponse;
import com.wu.euwallet.duplicatecheck.service.DuplicateCheckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("app/v2/dupcheck")
@RequiredArgsConstructor
public class DuplicateCheckController {

    private final DuplicateCheckService duplicateCheckService;

    @PostMapping()
    @LoggingAnnotation
    public ResponseEntity<ProfileUpdateResponse> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request) {

        ProfileUpdateResponse response = duplicateCheckService.updateProfile(request);
        return ResponseEntity.ok(response);
    }
}
