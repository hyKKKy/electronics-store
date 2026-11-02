package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Дані для реєстрації нового користувача
public record RegisterRequest(
        @NotBlank(message = "Email обов'язковий")
        @Email(message = "Некоректний email")
        @Size(max = 150, message = "Email не довший за 150 символів")
        String email,

        @NotBlank(message = "Пароль обов'язковий")
        @Size(min = 6, max = 100, message = "Пароль має містити від 6 до 100 символів")
        String password,

        @NotBlank(message = "Ім'я обов'язкове")
        @Size(max = 100, message = "Ім'я не довше за 100 символів")
        String name
) {
}
