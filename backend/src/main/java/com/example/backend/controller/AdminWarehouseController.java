package com.example.backend.controller;

import com.example.backend.dto.PageResponse;
import com.example.backend.dto.ProductResponse;
import com.example.backend.dto.StockAdjustmentRequest;
import com.example.backend.dto.StockIncomeRequest;
import com.example.backend.dto.StockMovementResponse;
import com.example.backend.dto.WarehouseSummaryResponse;
import com.example.backend.entity.MovementType;
import com.example.backend.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Облік товарів на складі (тільки ADMIN)
@RestController
@RequestMapping("/api/admin/warehouse")
@RequiredArgsConstructor
public class AdminWarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping("/summary")
    public WarehouseSummaryResponse summary(
            @RequestParam(name = "threshold", defaultValue = "5") int threshold) {
        return warehouseService.summary(threshold);
    }

    // Товари, які закінчуються: GET /api/admin/warehouse/low-stock?threshold=5
    @GetMapping("/low-stock")
    public List<ProductResponse> lowStock(
            @RequestParam(name = "threshold", defaultValue = "5") int threshold) {
        return warehouseService.lowStock(threshold);
    }

    // Журнал рухів: GET /api/admin/warehouse/movements?productId=1&type=INCOME
    @GetMapping("/movements")
    public PageResponse<StockMovementResponse> movements(
            @RequestParam(name = "productId", required = false) Long productId,
            @RequestParam(name = "type", required = false) MovementType type,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return warehouseService.movements(productId, type, pageable);
    }

    @PostMapping("/products/{id}/income")
    @ResponseStatus(HttpStatus.CREATED)
    public StockMovementResponse receive(@PathVariable("id") Long productId,
                                         @Valid @RequestBody StockIncomeRequest request) {
        return warehouseService.receive(productId, request);
    }

    @PostMapping("/products/{id}/adjustment")
    @ResponseStatus(HttpStatus.CREATED)
    public StockMovementResponse adjust(@PathVariable("id") Long productId,
                                        @Valid @RequestBody StockAdjustmentRequest request) {
        return warehouseService.adjust(productId, request);
    }
}
