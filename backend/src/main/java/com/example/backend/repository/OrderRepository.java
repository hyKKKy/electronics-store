package com.example.backend.repository;

import com.example.backend.entity.Order;
import com.example.backend.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Позиції та історія догружаються пачками (@BatchSize в Order)
    @EntityGraph(attributePaths = "user")
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Чи є у користувача замовлення (тоді його не можна видалити)
    boolean existsByUserId(Long userId);

    // Список замовлень для адміністратора з фільтром за статусом
    @EntityGraph(attributePaths = "user")
    @Query("select o from Order o where :status is null or o.status = :status")
    Page<Order> search(@Param("status") OrderStatus status, Pageable pageable);
}
