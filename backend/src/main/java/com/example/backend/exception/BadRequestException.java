package com.example.backend.exception;

// Запит коректний за форматом, але не має сенсу за бізнес-правилами -> HTTP 400
// (наприклад, невірний поточний пароль при зміні пароля)
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
