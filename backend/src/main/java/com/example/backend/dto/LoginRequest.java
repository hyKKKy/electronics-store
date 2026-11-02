package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email обов'язковий")
        String email,

        @NotBlank(message = "Пароль обов'язковий")
        String password
) {
}
