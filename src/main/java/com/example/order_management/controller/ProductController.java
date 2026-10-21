package com.example.order_management.controller;

import com.example.order_management.entity.ProductEntity;
import com.example.order_management.repository.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @GetMapping
    public List<ProductEntity> getProduct(){
        return productRepository.findAll();
    }
}
