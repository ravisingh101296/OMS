package com.example.order_management.repository;

import com.example.order_management.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    Optional<OrderEntity> findByIdAndUser_Id(UUID orderId,UUID userId);
}
