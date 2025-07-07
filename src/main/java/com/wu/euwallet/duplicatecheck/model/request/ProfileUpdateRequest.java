package com.wu.euwallet.duplicatecheck.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileUpdateRequest {

    @Email(message = "email must be a valid address")
    @JsonProperty("email")
    private String email;

    @JsonProperty("newPin")
    private String newPin;

    @NotBlank(message = "customer number is required")
    @JsonProperty("customerNumber")
    private String customerNumber;

    @NotBlank(message = "phone is required")
    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("phoneCountryCode")
    private String phoneCountryCode;

    @JsonProperty("address")
    private String address;

    @JsonProperty("customerId")
    private String customerId;

    @NotBlank(message = "firstName is mandatory")
    @JsonProperty("firstName")
    private String firstName;

    @NotBlank(message = "lastName is mandatory")
    @JsonProperty("lastName")
    private String lastName;


    @JsonProperty("customerUMN")
    private String customerUmn;

    @JsonProperty("deviceId")
    private String deviceId;

    @JsonProperty("externalReferenceId")
    private String externalReferenceId;

    @JsonProperty("correlationId")
    private String correlationId;

    @JsonProperty("sourceSystem")
    private String sourceSystem;

    @JsonProperty("channel")
    private String channel;

    @JsonProperty("subChannel")
    private String subChannel;

    @JsonProperty("upstreamServiceName")
    private String upstreamServiceName;

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("eventName")
    private String eventName;

    @Size(max = 10)
    @JsonProperty("language")
    private String language;
}