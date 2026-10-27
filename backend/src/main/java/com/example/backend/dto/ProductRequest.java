package com.example.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

// Дані, які приходять від клієнта при створенні/оновленні товару
public record ProductRequest(
        @NotBlank(message = "Назва обов'язкова")
        @Size(max = 200, message = "Назва не довша за 200 символів")
        String name,

        @Size(max = 2000, message = "Опис не довший за 2000 символів")
        String description,

        @Size(max = 100, message = "Бренд не довший за 100 символів")
        String brand,

        @NotNull(message = "Ціна обов'язкова")
        @DecimalMin(value = "0.01", message = "Ціна має бути більше 0")
        @Digits(integer = 8, fraction = 2, message = "Некоректний формат ціни")
        BigDecimal price,

        @NotNull(message = "Кількість обов'язкова")
        @Min(value = 0, message = "Кількість не може бути від'ємною")
        Integer stock,

        @NotNull(message = "Категорія обов'язкова")
        Long categoryId
) {
}
