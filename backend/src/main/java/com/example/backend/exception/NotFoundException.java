package com.example.backend.exception;

// Сутність не знайдена -> HTTP 404
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
