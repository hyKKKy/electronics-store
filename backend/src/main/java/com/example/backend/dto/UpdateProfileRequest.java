package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank(message = "Ім'я обов'язкове")
        @Size(max = 100, message = "Ім'я не довше за 100 символів")
        String name
) {
}
