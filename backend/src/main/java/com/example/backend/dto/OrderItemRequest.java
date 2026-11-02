package com.example.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
        @NotNull(message = "Товар обов'язковий")
        Long productId,

        @NotNull(message = "Кількість обов'язкова")
        @Min(value = 1, message = "Кількість має бути не менше 1")
        @Max(value = 100, message = "Кількість не більше 100")
        Integer quantity
) {
}
