package com.example.backend.dto;

import com.example.backend.entity.Order;
import com.example.backend.entity.OrderItem;
import com.example.backend.entity.OrderStatus;
import com.example.backend.entity.OrderStatusChange;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

// Замовлення з позиціями та історією статусів
public record OrderResponse(
        Long id,
        OrderStatus status,
        BigDecimal totalPrice,
        String address,
        LocalDateTime createdAt,
        Long userId,
        String userName,
        String userEmail,
        List<Item> items,
        List<StatusChange> statusHistory,
        // Куди адміністратор може перевести замовлення далі
        Set<OrderStatus> nextStatuses
) {

    public record Item(Long productId, String productName, Integer quantity, BigDecimal price, BigDecimal subtotal) {

        static Item from(OrderItem item) {
            return new Item(
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getPrice(),
                    item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }
    }

    public record StatusChange(OrderStatus status, LocalDateTime changedAt) {

        static StatusChange from(OrderStatusChange change) {
            return new StatusChange(change.getStatus(), change.getChangedAt());
        }
    }

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getAddress(),
                order.getCreatedAt(),
                order.getUser().getId(),
                order.getUser().getName(),
                order.getUser().getEmail(),
                order.getItems().stream().map(Item::from).toList(),
                order.getStatusHistory().stream().map(StatusChange::from).toList(),
                order.getStatus().nextStatuses()
        );
    }
}
