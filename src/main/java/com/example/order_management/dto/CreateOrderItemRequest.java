package com.example.order_management.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateOrderItemRequest(@NotNull (message = "Product ID is required")UUID id,
                                     @Positive(message = "Quantity must be greater then zero") int quantity) {
}
