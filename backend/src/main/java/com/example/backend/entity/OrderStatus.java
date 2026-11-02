package com.example.backend.entity;

import java.util.Set;

public enum OrderStatus {
    NEW,        // замовлення створене
    CONFIRMED,  // підтверджене адміністратором
    SHIPPED,    // відправлене
    DELIVERED,  // доставлене
    CANCELLED;  // скасоване

    // Дозволені переходи між статусами. DELIVERED і CANCELLED — кінцеві.
    public Set<OrderStatus> nextStatuses() {
        return switch (this) {
            case NEW -> Set.of(CONFIRMED, CANCELLED);
            case CONFIRMED -> Set.of(SHIPPED, CANCELLED);
            case SHIPPED -> Set.of(DELIVERED);
            case DELIVERED, CANCELLED -> Set.of();
        };
    }

    public boolean canChangeTo(OrderStatus next) {
        return nextStatuses().contains(next);
    }
}
