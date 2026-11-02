package com.example.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Надходження товару на склад
public record StockIncomeRequest(
        @NotNull(message = "Кількість обов'язкова")
        @Min(value = 1, message = "Кількість має бути не менше 1")
        @Max(value = 100000, message = "Кількість не більше 100000")
        Integer quantity,

        @Size(max = 255, message = "Коментар не довший за 255 символів")
        String comment
) {
}
