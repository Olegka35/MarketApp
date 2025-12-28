package com.tarasov.market.model.dto;

import jakarta.validation.constraints.NotBlank;

public record RegistrationRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
