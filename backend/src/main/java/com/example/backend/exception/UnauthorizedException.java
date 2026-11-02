package com.example.backend.exception;

// Невірні облікові дані -> HTTP 401
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
