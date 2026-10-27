package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Дані, які приходять від клієнта при створенні/оновленні категорії
public record CategoryRequest(
        @NotBlank(message = "Назва обов'язкова")
        @Size(max = 100, message = "Назва не довша за 100 символів")
        String name,

        @Size(max = 500, message = "Опис не довший за 500 символів")
        String description
) {
}
