package com.example.order_management.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="idempotency_records")
public class IdempotencyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false,unique = true)
    private String idempotencyKey;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false, updatable = false)
    private Instant createAt = Instant.now();

    @Column(nullable = false, length = 64)
    private String requestHash;

    protected IdempotencyRecord(){}

    public IdempotencyRecord( String idempotencyKey, UUID orderId, String requestHash) {
        this.idempotencyKey = idempotencyKey;
        this.orderId = orderId;
        this.requestHash = requestHash;
    }

    public UUID getId() {
        return id;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public Instant getCreateAt() {
        return createAt;
    }

    public String getRequestHash() {
        return requestHash;
    }
}
