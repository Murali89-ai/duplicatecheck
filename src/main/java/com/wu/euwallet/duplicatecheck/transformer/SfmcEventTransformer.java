package com.wu.euwallet.duplicatecheck.transformer;

import com.wu.euwallet.duplicatecheck.model.request.ProfileUpdateRequest;
import com.wu.euwallet.duplicatecheck.model.request.sfmc.SfmcEventRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SfmcEventTransformer {

    private static final String EVENT_DEFINITION_KEY = "PROFILE_UPDATE_EVENT";

    public SfmcEventRequest build(ProfileUpdateRequest request) {
        Map<String, Object> data = new HashMap<>();
        data.put("customerNumber", request.getCustomerNumber());
        data.put("mobileNumber", request.getPhoneNumber());
        data.put("emailId", request.getEmail());
        data.put("firstName", request.getFirstName());
        data.put("lastName", request.getLastName());
        data.put("language", request.getLanguage());
        data.put("channel", request.getChannel());

        return SfmcEventRequest.builder()
                .contactKey(request.getCustomerNumber())
                .eventDefinitionKey(EVENT_DEFINITION_KEY)
                .data(data)
                .build();
    }
}
