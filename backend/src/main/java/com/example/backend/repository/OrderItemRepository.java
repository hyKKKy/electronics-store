package com.example.backend.repository;

import com.example.backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Чи є товар хоча б в одному замовленні (тоді його не можна видалити)
    boolean existsByProductId(Long productId);
}
