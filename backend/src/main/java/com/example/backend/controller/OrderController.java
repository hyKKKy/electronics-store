package com.example.backend.controller;

import com.example.backend.dto.CreateOrderRequest;
import com.example.backend.dto.OrderResponse;
import com.example.backend.security.AuthUser;
import com.example.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Замовлення поточного користувача
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@AuthenticationPrincipal AuthUser authUser,
                                @Valid @RequestBody CreateOrderRequest request) {
        return orderService.create(authUser.id(), request);
    }

    @GetMapping
    public List<OrderResponse> myOrders(@AuthenticationPrincipal AuthUser authUser) {
        return orderService.findByUser(authUser.id());
    }

    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable("id") Long id, @AuthenticationPrincipal AuthUser authUser) {
        return orderService.findById(id, authUser);
    }

    @PostMapping("/{id}/cancel")
    public OrderResponse cancel(@PathVariable("id") Long id, @AuthenticationPrincipal AuthUser authUser) {
        return orderService.cancelByUser(id, authUser);
    }
}
