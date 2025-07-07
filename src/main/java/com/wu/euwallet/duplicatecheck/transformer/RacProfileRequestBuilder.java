package com.wu.euwallet.duplicatecheck.transformer;

import com.wu.euwallet.duplicatecheck.model.request.ProfileUpdateRequest;
import com.wu.euwallet.duplicatecheck.model.request.rac.RacProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RacProfileRequestBuilder {

    public RacProfileRequest build(ProfileUpdateRequest request) {
        return RacProfileRequest.builder()
                .customerNumber(request.getCustomerNumber())
                .emailId(request.getEmail())
                .mobileNumber(request.getPhoneNumber())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .language(request.getLanguage())
                .channel(request.getChannel())
                .build();
    }
}
