package com.wu.euwallet.duplicatecheck.transformer;

import com.wu.euwallet.duplicatecheck.model.common.kafka.ProfileUpdatePayload;
import com.wu.euwallet.duplicatecheck.model.request.ProfileUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Component
public class ProfileUpdateTransformer {

    public ProfileUpdatePayload transform(ProfileUpdateRequest request) {
        return ProfileUpdatePayload.builder()
                .transactionId(UUID.randomUUID().toString())
                .customerNumber(request.getCustomerNumber())
                .channel(request.getChannel())
                .deviceId(request.getDeviceId())
                .updatedTime(OffsetDateTime.now().toString())
                .eventType("PROFILE_UPDATE")
                .sourceSystem("duplicate-check")
                .build();
    }
}
