package com.example.order_management.repository;

import com.example.order_management.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, UUID> {
    Optional<IdempotencyRecord> findByIdempotencyKey(
            String idempotencyKey
    );
}
