package com.example.order_management;

import com.example.order_management.entity.ProductEntity;
import com.example.order_management.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ProductDataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    public ProductDataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        List<ProductEntity> products = List.of(
                new ProductEntity(
                        "Mechanical Keyboard",
                        new BigDecimal("79.99"),
                        10
                ),
                new ProductEntity(
                        "Wireless Mouse",
                        new BigDecimal("29.99"),
                        20
                ),
                new ProductEntity(
                        "USB-C Hub",
                        new BigDecimal("49.99"),
                        5
                )
        );

        productRepository.saveAll(products);

        System.out.println("Sample products initialized.");
    }
}