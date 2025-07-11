package com.wu.euwallet.duplicatecheck.transformer;

import com.wu.euwallet.duplicatecheck.config.SFMCProperties;
import com.wu.euwallet.duplicatecheck.model.common.kafka.TransactionData;
import com.wu.euwallet.duplicatecheck.model.request.ProfileUpdateRequest;
import com.wu.euwallet.duplicatecheck.model.request.mambu.MambuUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.stream.Collectors;


import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ProfileUpdateRequestTransformer {

    private final SFMCProperties sfmcProperties;

    public MambuUpdateRequest toMambuRequest(ProfileUpdateRequest request, TransactionData txData) {
        return MambuUpdateRequest.builder()
                .customerId(request.getPartyId())
                .eventId(txData.getTransactionId())
                .reason("Profile update completed") // or extract dynamically if needed
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    public JsonNode toSfmcRequest(ProfileUpdateRequest request, TransactionData txData, JsonNode ucdLookupResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode root = objectMapper.createObjectNode();

        root.put("customerUmn", request.getCustomerUmn());
        root.put("eventDefinitionKey", sfmcProperties.getEventDefinitionKey());

        ObjectNode data = objectMapper.createObjectNode();

        boolean hasPhone = request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank();
        boolean hasEmail = request.getEmail() != null && !request.getEmail().isBlank();

        String messageKey = hasPhone
                ? sfmcProperties.getMessageKeyPhone()
                : sfmcProperties.getMessageKeyEmail();

        data.put("messageKey", messageKey);

        if (hasPhone) {
            JsonNode phones = ucdLookupResponse.at("/customerDetails/0/customerPhone");

            String phoneNumberOld = phones.findValues("phoneNumber").stream()
                    .map(JsonNode::asText)
                    .collect(Collectors.joining());

            String phoneNumberCodeOld = phones.findValues("isdCode").stream()
                    .map(JsonNode::asText)
                    .collect(Collectors.joining());

            data.put("phoneNumberOld", phoneNumberOld);
            data.put("phoneNumberCodeOld", phoneNumberCodeOld);
        }

        if (hasEmail) {
            JsonNode email = ucdLookupResponse.at("/customerDetails/0/customerEmail/0/email/emailValue");
            data.put("emailAddressOld", email.asText(""));
        }

        root.set("data", data);
        return root;
    }
}
