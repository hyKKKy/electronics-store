package com.example.backend.exception;

// Операція суперечить поточному стану даних -> HTTP 409
// (наприклад, категорія з такою назвою вже існує)
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
