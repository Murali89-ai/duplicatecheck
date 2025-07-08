package com.wu.euwallet.duplicatecheck.kafka;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class KafkaHeadersUtil {

    private KafkaHeadersUtil() { /* util class */ }

    /**
     * Builds standard Kafka headers with null-safe external reference id.
     */
    public static Headers buildStandardHeaders(String correlationId, String externalRefId, String sourceSystem) {

        Headers headers = new RecordHeaders();

        headers.add("correlationId", correlationId.getBytes(StandardCharsets.UTF_8));
        headers.add("externalReferenceId", Optional.ofNullable(externalRefId).orElse("N/A").getBytes(StandardCharsets.UTF_8));
        headers.add("sourceSystem", sourceSystem.getBytes(StandardCharsets.UTF_8));

        return headers;
    }
}
