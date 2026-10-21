package com.example.order_management.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    @Version
    private Long version;

    protected ProductEntity(){}

    public ProductEntity(String name, BigDecimal price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public void decreaseStock(int quantity){
        if(quantity<0){
            throw new IllegalStateException("Quantity should be greater then Zero");
        }

        if(stock<quantity){
            throw new IllegalStateException("Insufficient stock for product "+ name);
        }

            stock-= quantity;

    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }


}
