package com.wu.euwallet.duplicatecheck.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.euwallet.duplicatecheck.aop.LoggingAnnotation;
import com.wu.euwallet.duplicatecheck.adaptor.MarqetaAdaptor;
import com.wu.euwallet.duplicatecheck.config.KafkaTopicsConfig;
import com.wu.euwallet.duplicatecheck.constants.DuplicateCheckConstants;
import com.wu.euwallet.duplicatecheck.kafka.producer.ProfileUpdateKafkaProducer;
import com.wu.euwallet.duplicatecheck.model.kafka.DuplicateCheckKafkaEvent;
import com.wu.euwallet.duplicatecheck.service.MarqetaUpdateService;
import com.wu.euwallet.duplicatecheck.validation.RequestValidator;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarqetaUpdateServiceImpl implements MarqetaUpdateService {

    private final MarqetaAdaptor              marqetaAdaptor;
    private final KafkaTopicsConfig           kafkaTopics;
    private final ProfileUpdateKafkaProducer  kafkaProducer;
    private final RequestValidator            validator;
    private final ObjectMapper                mapper;

    /** Consume the DLQ message (or a direct call) and invoke Marqeta. */
    @Override
    @LoggingAnnotation
    public JsonNode process(@NotBlank @NonNull String rawJson) {

        String correlationId = UUID.randomUUID().toString();
        String externalRefId = "N/A";

        try {
            /* 1️⃣ Parse and validate */
            JsonNode root        = mapper.readTree(rawJson);
            String   cardToken   = root.path("cardToken").asText();
            JsonNode updateBody  = root.path("updatePayload");
            externalRefId        = root.path("externalRefId").asText(null);

            validator.notBlank(cardToken,   "cardToken must not be blank");
            validator.notNull (updateBody,  "updatePayload is mandatory");

            /* 2️⃣ Marqeta update */
            JsonNode marqetaResponse = marqetaAdaptor.updateCard(cardToken, updateBody);
            log.info("Marqeta update OK for cardToken={} -> {}", cardToken, marqetaResponse);

            /* 3️⃣ Build success event -> Kafka */
            DuplicateCheckKafkaEvent event = DuplicateCheckKafkaEvent.builder()
                    .correlationId(correlationId)
                    .externalRefId(externalRefId)
                    .status(DuplicateCheckConstants.DUPLICATE_CHECK_SUCCESS_CODE)
                    .message("Marqeta card updated")
                    .sourceSystem("MARQETA")
                    .eventType("BUSINESS_EVENT")
                    .eventTime(nowIso())
                    .build();

            kafkaProducer.sendBusinessEvent(event, buildHeaders(correlationId, externalRefId));

            return marqetaResponse;

        } catch (Exception ex) {

            log.error("Marqeta update failed – publishing error event", ex);

            com.wu.euwallet.duplicatecheck.model.kafka.DuplicateCheckKafkaEvent errorEvent = DuplicateCheckKafkaEvent.builder()
                    .correlationId(correlationId)
                    .externalRefId(externalRefId)
                    .status(DuplicateCheckConstants.DUPLICATE_CHECK_ERROR_CODE)
                    .message(ex.getMessage())
                    .sourceSystem("MARQETA")
                    .eventType("ERROR_EVENT")
                    .eventTime(nowIso())
                    .build();

            try {
                kafkaProducer.sendErrorEvent(errorEvent, buildHeaders(correlationId, externalRefId));
            } catch (Exception e2) {
                log.error("Secondary failure – unable to push error event to Kafka", e2);
            }

            throw new RuntimeException("Marqeta update failed", ex);
        }
    }

    /* ───────────────────────── Helpers ───────────────────────── */

    private static Headers buildHeaders(String correlationId, String externalRefId) {
        Headers h = new RecordHeaders();
        h.add("correlationId",       correlationId.getBytes(StandardCharsets.UTF_8));
        h.add("externalReferenceId", externalRefId.getBytes(StandardCharsets.UTF_8));
        h.add("sourceSystem",        "marqeta-update-svc".getBytes(StandardCharsets.UTF_8));
        return h;
    }

    private static String nowIso() {
        return OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
