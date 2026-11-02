package com.example.backend.dto;

// Відповідь на вхід/реєстрацію: токен для заголовка Authorization + дані користувача
public record AuthResponse(String token, UserResponse user) {
}
