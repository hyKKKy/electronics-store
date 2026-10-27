package com.example.backend.dto;

import com.example.backend.entity.Category;

// Дані категорії, які сервер віддає клієнту
public record CategoryResponse(Long id, String name, String description) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }
}
