package com.example.backend.repository;

import com.example.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    // Для оновлення: чи є ІНША категорія з такою назвою
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
