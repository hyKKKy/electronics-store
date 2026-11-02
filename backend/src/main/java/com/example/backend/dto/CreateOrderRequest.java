package com.example.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

// Оформлення замовлення: адреса доставки + товари з кошика.
// Ціни з клієнта не приймаємо — вони беруться з бази.
public record CreateOrderRequest(
        @NotBlank(message = "Адреса доставки обов'язкова")
        @Size(max = 500, message = "Адреса не довша за 500 символів")
        String address,

        @NotEmpty(message = "Замовлення не може бути порожнім")
        @Size(max = 50, message = "Не більше 50 позицій в замовленні")
        List<@Valid OrderItemRequest> items
) {
}
