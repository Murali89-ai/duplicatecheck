package com.wu.euwallet.duplicatecheck.kafka;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;

public class KafkaHeadersUtil {

    public static Headers buildStandardHeaders(String correlationId, String externalReferenceId, String sourceSystem) {
        Headers headers = new RecordHeaders();
        headers.add("correlationId", correlationId.getBytes());
        headers.add("externalReferenceId", externalReferenceId.getBytes());
        headers.add("sourceSystem", sourceSystem.getBytes());
        return headers;
    }
}