package com.wu.euwallet.duplicatecheck.transformer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.euwallet.duplicatecheck.config.UcdConfig;
import com.wu.euwallet.duplicatecheck.model.request.ProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class UcdUpdateRequestBuilder {
    private final ObjectMapper mapper;
    private final UcdConfig cfg;
    public ObjectNode buildLookup(ProfileUpdateRequest req) {
        ObjectNode root = mapper.createObjectNode();
        root.put("customerNumber", req.getCustomerNumber());
        root.put("email", req.getEmail());
        root.put("phoneNumber", req.getPhoneNumber());
        return root;
    }
    public ObjectNode buildUpdate(ProfileUpdateRequest req, String transactionId) {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode header = root.putObject("header");
        header.put("source", "DUPCHECK");
        header.put("appName", cfg.getAppName());
        header.put("appVersion", cfg.getAppVersion());
        header.put("trasactionId", transactionId);

        ObjectNode cust = root.putObject("customer");
        cust.put("customerNumber", req.getCustomerNumber());
        cust.put("email", req.getEmail());
        cust.put("phoneNumber", req.getPhoneNumber());
        cust.put("requestInitiatedBy", cfg.getRequestedBy());
        return root;
    }
}
