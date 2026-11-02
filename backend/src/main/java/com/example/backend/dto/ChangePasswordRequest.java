package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "Поточний пароль обов'язковий")
        String currentPassword,

        @NotBlank(message = "Новий пароль обов'язковий")
        @Size(min = 6, max = 100, message = "Пароль має містити від 6 до 100 символів")
        String newPassword
) {
}
