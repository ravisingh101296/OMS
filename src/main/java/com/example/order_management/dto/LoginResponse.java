package com.example.order_management.dto;

public record LoginResponse(String accessToken,
                            String tokenType,
                            long expiresIn) {
}
