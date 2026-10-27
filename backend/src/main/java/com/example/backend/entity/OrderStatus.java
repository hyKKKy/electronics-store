package com.example.backend.entity;

public enum OrderStatus {
    NEW,        // замовлення створене
    CONFIRMED,  // підтверджене адміністратором
    SHIPPED,    // відправлене
    DELIVERED,  // доставлене
    CANCELLED   // скасоване
}
