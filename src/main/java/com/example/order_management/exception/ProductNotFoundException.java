package com.example.order_management.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(UUID id){
        super("Product not found with id: "+ id);
    }
}
