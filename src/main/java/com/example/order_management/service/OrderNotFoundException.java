package com.example.order_management.service;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(UUID id){
        super("Order not found with id, "+ id);
    }
}
