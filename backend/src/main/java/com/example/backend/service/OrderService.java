package com.example.backend.service;

import com.example.backend.dto.CreateOrderRequest;
import com.example.backend.dto.OrderItemRequest;
import com.example.backend.dto.OrderResponse;
import com.example.backend.dto.PageResponse;
import com.example.backend.entity.Order;
import com.example.backend.entity.OrderItem;
import com.example.backend.entity.OrderStatus;
import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.NotFoundException;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // Оформлення замовлення: перевіряємо наявність, списуємо товар зі складу,
    // фіксуємо ціни на момент покупки
    @Transactional
    public OrderResponse create(Long userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Користувача з id=" + userId + " не знайдено"));

        // Якщо один товар прийшов кількома рядками — складаємо кількість
        Map<Long, Integer> quantities = new LinkedHashMap<>();
        for (OrderItemRequest item : request.items()) {
            quantities.merge(item.productId(), item.quantity(), Integer::sum);
        }

        Map<Long, Product> products = productRepository.findAllByIdForUpdate(quantities.keySet()).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        Order order = new Order();
        order.setUser(user);
        order.setAddress(request.address().trim());
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {
            Product product = products.get(entry.getKey());
            if (product == null) {
                throw new NotFoundException("Товар з id=" + entry.getKey() + " не знайдено");
            }
            int quantity = entry.getValue();
            if (product.getStock() < quantity) {
                throw new ConflictException("Недостатньо товару '" + product.getName()
                        + "' на складі: доступно " + product.getStock() + ", замовлено " + quantity);
            }
            product.setStock(product.getStock() - quantity);

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPrice(product.getPrice());
            order.addItem(item);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        order.setTotalPrice(total);
        order.changeStatus(OrderStatus.NEW);
        return OrderResponse.from(orderRepository.save(order));
    }

    // Замовлення поточного користувача, від нових до старих
    @Transactional(readOnly = true)
    public List<OrderResponse> findByUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(OrderResponse::from)
                .toList();
    }

    // Користувач бачить лише свої замовлення, адміністратор — будь-які
    @Transactional(readOnly = true)
    public OrderResponse findById(Long id, AuthUser authUser) {
        return OrderResponse.from(getAccessibleOrder(id, authUser));
    }

    // Користувач може скасувати своє замовлення, поки його не підтвердили
    @Transactional
    public OrderResponse cancelByUser(Long id, AuthUser authUser) {
        Order order = getAccessibleOrder(id, authUser);
        if (order.getStatus() != OrderStatus.NEW) {
            throw new BadRequestException("Скасувати можна лише нове замовлення");
        }
        cancel(order);
        return OrderResponse.from(order);
    }

    // ---------- Адміністрування ----------

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> search(OrderStatus status, Pageable pageable) {
        return PageResponse.from(orderRepository.search(status, pageable).map(OrderResponse::from));
    }

    @Transactional
    public OrderResponse changeStatus(Long id, OrderStatus newStatus) {
        Order order = getOrder(id);
        if (!order.getStatus().canChangeTo(newStatus)) {
            throw new BadRequestException("Неможливо змінити статус з " + order.getStatus() + " на " + newStatus);
        }
        if (newStatus == OrderStatus.CANCELLED) {
            cancel(order);
        } else {
            order.changeStatus(newStatus);
        }
        return OrderResponse.from(order);
    }

    // Скасування повертає товари на склад
    private void cancel(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
        }
        order.changeStatus(OrderStatus.CANCELLED);
    }

    private Order getAccessibleOrder(Long id, AuthUser authUser) {
        Order order = getOrder(id);
        // Чуже замовлення — як неіснуюче, щоб не розкривати чужі id
        if (!authUser.isAdmin() && !order.getUser().getId().equals(authUser.id())) {
            throw new NotFoundException("Замовлення з id=" + id + " не знайдено");
        }
        return order;
    }

    private Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Замовлення з id=" + id + " не знайдено"));
    }
}
