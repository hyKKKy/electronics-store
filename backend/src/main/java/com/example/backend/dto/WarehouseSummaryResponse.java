package com.example.backend.dto;

import java.math.BigDecimal;

// Загальний стан складу
public record WarehouseSummaryResponse(
        long productCount,     // кількість позицій у каталозі
        long totalUnits,       // загальна кількість одиниць на складі
        BigDecimal totalValue, // вартість залишків за поточними цінами
        long outOfStockCount,  // позицій, яких немає в наявності
        long lowStockCount     // позицій із залишком нижче порогу
) {
}
