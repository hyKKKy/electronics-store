package com.example.backend.dto;

import com.example.backend.entity.Product;

import java.math.BigDecimal;

// Дані товару, які сервер віддає клієнту (разом із назвою категорії)
public record ProductResponse(
        Long id,
        String name,
        String description,
        String brand,
        BigDecimal price,
        Integer stock,
        Long categoryId,
        String categoryName
) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getBrand(),
                product.getPrice(),
                product.getStock(),
                product.getCategory().getId(),
                product.getCategory().getName()
        );
    }
}
