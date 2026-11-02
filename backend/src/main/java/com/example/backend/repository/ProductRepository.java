package com.example.backend.repository;

import com.example.backend.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
              and (:brand is null or lower(p.brand) = lower(:brand))
              and (:minPrice is null or p.price >= :minPrice)
              and (:maxPrice is null or p.price <= :maxPrice)
              and (:inStock = false or p.stock > 0)
            """)
    Page<Product> search(@Param("search") String search,
                         @Param("categoryId") Long categoryId,
                         @Param("brand") String brand,
                         @Param("minPrice") BigDecimal minPrice,
                         @Param("maxPrice") BigDecimal maxPrice,
                         @Param("inStock") boolean inStock,
                         Pageable pageable);

    // Список брендів для фільтра в каталозі
    @Query("select distinct p.brand from Product p where p.brand is not null and p.brand <> '' order by p.brand")
    List<String> findAllBrands();

    boolean existsByCategoryId(Long categoryId);

    // Блокує рядки товарів до кінця транзакції, щоб два одночасні замовлення
    // не продали один і той самий останній товар
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id in :ids")
    List<Product> findAllByIdForUpdate(@Param("ids") Collection<Long> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

    // Товари із залишком не більше порогу — що пора дозамовити
    @EntityGraph(attributePaths = "category")
    List<Product> findByStockLessThanEqualOrderByStockAscNameAsc(Integer threshold);

    long countByStock(Integer stock);

    long countByStockLessThanEqual(Integer threshold);

    @Query("select coalesce(sum(p.stock), 0) from Product p")
    long sumStock();

    @Query("select coalesce(sum(p.price * p.stock), 0) from Product p")
    BigDecimal sumStockValue();
}
