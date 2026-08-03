package com.momento.service.dto.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GuestSearchRequest {
    @NotNull
    private Boolean consentAccepted;
    private String consentPolicyVersion;
    @NotBlank
    private String selfie; // Base64 encoded selfie
}
