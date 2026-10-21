package com.example.order_management.dto;

import java.util.UUID;

public record UserResponse(UUID id,
                           String email) {
}
