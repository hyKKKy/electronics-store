package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// "order" — зарезервоване слово в SQL, тому таблиця "orders"
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.NEW;

    // Рахується на сервері із позицій замовлення
    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // cascade = ALL — позиції зберігаються/видаляються разом із замовленням.
    // @BatchSize — для списку замовлень позиції підтягуються одним запитом на пачку, а не на кожне замовлення
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    private List<OrderItem> items = new ArrayList<>();

    // Історія змін статусу, від найстарішого до найновішого
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("changedAt asc, id asc")
    @BatchSize(size = 50)
    private List<OrderStatusChange> statusHistory = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Додає позицію і одразу проставляє зворотний зв'язок
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    // Змінює статус і записує зміну в історію
    public void changeStatus(OrderStatus newStatus) {
        this.status = newStatus;
        OrderStatusChange change = new OrderStatusChange(newStatus);
        change.setOrder(this);
        statusHistory.add(change);
    }
}
