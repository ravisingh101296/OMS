package com.example.order_management.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )


    private List<OrderItemEntity> items = new ArrayList<>();

    public OrderEntity() {
        // Required by JPA
    }
    public void addItem(OrderItemEntity item) {
        items.add(item);
    }

    public List<OrderItemEntity> getItems() {
        return List.copyOf(items);
    }

    public void setTotalAmount(BigDecimal totalAmount){
        this.totalAmount = totalAmount;
    }

    public UUID getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
