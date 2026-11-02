package com.example.backend.dto;

import com.example.backend.entity.MovementType;
import com.example.backend.entity.StockMovement;

import java.time.LocalDateTime;

public record StockMovementResponse(
        Long id,
        Long productId,
        String productName,
        MovementType type,
        Integer quantity,
        Integer stockAfter,
        Long orderId,
        String comment,
        LocalDateTime createdAt
) {

    public static StockMovementResponse from(StockMovement movement) {
        return new StockMovementResponse(
                movement.getId(),
                movement.getProduct().getId(),
                movement.getProduct().getName(),
                movement.getType(),
                movement.getQuantity(),
                movement.getStockAfter(),
                movement.getOrder() == null ? null : movement.getOrder().getId(),
                movement.getComment(),
                movement.getCreatedAt()
        );
    }
}
