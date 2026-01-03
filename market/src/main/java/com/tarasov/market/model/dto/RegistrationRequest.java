package com.tarasov.market.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @NotBlank String username,
        @NotBlank @Size(min = 8) String password
) {
}
