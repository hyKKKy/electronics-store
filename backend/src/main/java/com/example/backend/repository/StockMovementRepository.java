package com.example.backend.repository;

import com.example.backend.entity.MovementType;
import com.example.backend.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    @EntityGraph(attributePaths = {"product", "order"})
    @Query("""
            select m from StockMovement m
            where (:productId is null or m.product.id = :productId)
              and (:type is null or m.type = :type)
            """)
    Page<StockMovement> search(@Param("productId") Long productId,
                               @Param("type") MovementType type,
                               Pageable pageable);

    // Перед видаленням товару прибираємо його історію на складі
    @Modifying
    @Query("delete from StockMovement m where m.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}
