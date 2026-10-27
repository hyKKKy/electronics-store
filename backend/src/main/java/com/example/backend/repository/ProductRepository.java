package com.example.backend.repository;

import com.example.backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Пошук і фільтрація каталогу. Якщо параметр = null, фільтр не застосовується.
    // @EntityGraph одразу підтягує категорію, щоб не було зайвого запиту на кожен товар (N+1).
    @EntityGraph(attributePaths = "category")
    @Query("""
            select p from Product p
            where (:search is null
                   or lower(p.name) like lower(concat('%', :search, '%'))
                   or lower(p.brand) like lower(concat('%', :search, '%')))
              and (:categoryId is null or p.category.id = :categoryId)
              and (:minPrice is null or p.price >= :minPrice)
              and (:maxPrice is null or p.price <= :maxPrice)
            """)
    Page<Product> search(@Param("search") String search,
                         @Param("categoryId") Long categoryId,
                         @Param("minPrice") BigDecimal minPrice,
                         @Param("maxPrice") BigDecimal maxPrice,
                         Pageable pageable);

    boolean existsByCategoryId(Long categoryId);
}
