package com.wu.euwallet.duplicatecheck.transformer;

import com.wu.euwallet.duplicatecheck.model.common.kafka.ErrorEvent;
import com.wu.euwallet.duplicatecheck.model.common.kafka.ErrorEventHeader;
import com.wu.euwallet.duplicatecheck.constants.BusinessEventConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ErrorEventTransformer {

    public ErrorEvent build(String errorCode, String errorMessage, String customerNumber) {
        ErrorEventHeader header = ErrorEventHeader.builder()
                .eventName("PROFILE_UPDATE_ERROR")
                .eventType(BusinessEventConstants.EVENT_TYPE)
                .eventVersion(BusinessEventConstants.EVENT_VERSION)
                .eventTime(ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT))
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .trackingId(UUID.randomUUID().toString())
                .build();

        return ErrorEvent.builder()
                .header(header)
                .customerNumber(customerNumber)
                .build();
    }
}
