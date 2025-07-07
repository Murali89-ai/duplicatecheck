package com.wu.euwallet.duplicatecheck.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.euwallet.duplicatecheck.constants.BusinessEventConstants;
import com.wu.euwallet.duplicatecheck.model.common.kafka.BusinessEvent;
import com.wu.euwallet.duplicatecheck.model.common.kafka.BusinessEventHeader;
import com.wu.euwallet.duplicatecheck.model.common.kafka.ProfileUpdatePayload;
import com.wu.euwallet.duplicatecheck.model.request.ProfileUpdateRequest;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class ProfileUpdateEventTransformer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public BusinessEvent transform(ProfileUpdateRequest request) {
        ProfileUpdatePayload payload = ProfileUpdatePayload.builder()
                .customerNumber(request.getCustomerNumber())
                .mobileNumber(request.getPhoneNumber())
                .emailId(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .language(request.getLanguage())
                .channel(request.getChannel())
                .transactionId(UUID.randomUUID().toString())
                .build();

        BusinessEventHeader header = BusinessEventHeader.builder()
                .eventName(BusinessEventConstants.PROFILE_UPDATE_EVENT_NAME)
                .eventType(BusinessEventConstants.EVENT_TYPE)
                .eventVersion(BusinessEventConstants.EVENT_VERSION)
                .eventTime(ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT))
                .build();

        return BusinessEvent.builder()
                .header(header)
                .payload(payload)
                .build();
    }
}