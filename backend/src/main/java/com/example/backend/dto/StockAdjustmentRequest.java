package com.example.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Інвентаризація: задаємо фактичний залишок, різниця записується в журнал
public record StockAdjustmentRequest(
        @NotNull(message = "Фактичний залишок обов'язковий")
        @Min(value = 0, message = "Залишок не може бути від'ємним")
        @Max(value = 1000000, message = "Залишок не більше 1000000")
        Integer actualStock,

        @Size(max = 255, message = "Коментар не довший за 255 символів")
        String comment
) {
}
