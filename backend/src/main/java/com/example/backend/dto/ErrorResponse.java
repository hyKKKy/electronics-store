package com.example.backend.dto;

import java.util.Map;

// Єдиний формат відповіді з помилкою.
// errors — помилки по полях (наприклад {"price": "Ціна має бути більше 0"})
public record ErrorResponse(int status, String message, Map<String, String> errors) {
}
