package com.example.backend.controller;

import com.example.backend.dto.ChangeOrderStatusRequest;
import com.example.backend.dto.OrderResponse;
import com.example.backend.dto.PageResponse;
import com.example.backend.entity.OrderStatus;
import com.example.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Керування замовленнями (тільки ADMIN)
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    // Приклад: GET /api/admin/orders?status=NEW&page=0&size=20
    @GetMapping
    public PageResponse<OrderResponse> search(
            @RequestParam(name = "status", required = false) OrderStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return orderService.search(status, pageable);
    }

    @PatchMapping("/{id}/status")
    public OrderResponse changeStatus(@PathVariable("id") Long id,
                                      @Valid @RequestBody ChangeOrderStatusRequest request) {
        return orderService.changeStatus(id, request.status());
    }
}
