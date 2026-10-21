package com.example.order_management.repository;

import com.example.order_management.entity.ProductEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductEntity p WHERE p.id = :id")
    Optional<ProductEntity> findByIdForUpdate(
            @Param("id") UUID id
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductEntity p WHERE p.id IN:ids ORDER BY p.id")
    List<ProductEntity> findALLByIdForUpdate(
            @Param("ids") Collection<UUID> ids
    );
}
