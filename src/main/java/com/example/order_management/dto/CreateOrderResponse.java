package com.example.order_management.dto;

import com.example.order_management.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateOrderResponse(UUID id,
                                  OrderStatus status,
                                  BigDecimal totalAmount,
                                  Instant createdAt) {
}
