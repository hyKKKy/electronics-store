package com.example.backend.dto;

import com.example.backend.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeOrderStatusRequest(
        @NotNull(message = "Статус обов'язковий")
        OrderStatus status
) {
}
