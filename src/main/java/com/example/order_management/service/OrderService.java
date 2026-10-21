package com.example.order_management.service;

import com.example.order_management.dto.*;
import com.example.order_management.entity.*;
import com.example.order_management.exception.InsufficientStockException;
import com.example.order_management.exception.ProductNotFoundException;
import com.example.order_management.repository.IdempotencyRecordRepository;
import com.example.order_management.repository.OrderRepository;
import com.example.order_management.repository.ProductRepository;

import com.example.order_management.repository.UserRepository;
import com.example.order_management.util.RequestHashUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final OrderCreationTransactionService orderCreationTransactionService;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, IdempotencyRecordRepository idempotencyRecordRepository, OrderCreationTransactionService orderCreationTransactionService, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.idempotencyRecordRepository = idempotencyRecordRepository;
        this.orderCreationTransactionService = orderCreationTransactionService;
        this.userRepository = userRepository;
    }

    public OrderEntity getOrderById(UUID id){
        return orderRepository.findById(id).orElseThrow(()->new OrderNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderDetailsById(UUID id) {
        UUID userId = getAuthenticatedUserId();

        OrderEntity order = orderRepository.findByIdAndUser_Id(id,userId)
                .orElseThrow(() -> new OrderNotFoundException(id));

        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                items
        );
    }


    public OrderEntity createOrder(CreateOrderRequest request,String Idempotencykey) {
        try{
            UUID userId = getAuthenticatedUserId();

            return orderCreationTransactionService.createOrder(request,Idempotencykey,userId);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            return recoverDuplicateRequest(request, Idempotencykey, ex);
        }

    }
    private OrderEntity recoverDuplicateRequest(
            CreateOrderRequest request,
            String idempotencyKey,
            RuntimeException originalException
    ) {
        var existingRecord = idempotencyRecordRepository
                .findByIdempotencyKey(idempotencyKey);

        if (existingRecord.isEmpty()) {
            // This wasn't a recoverable duplicate idempotency key.
            throw originalException;
        }

        var record = existingRecord.get();
        String requestHash = RequestHashUtil.hash(request);

        if (!record.getRequestHash().equals(requestHash)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Idempotency key was already used with a different request"
            );
        }

        return orderRepository.findById(record.getOrderId())
                .orElseThrow(() -> new IllegalStateException(
                        "Idempotency record references a missing order"
                ));
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }

        return UUID.fromString(authentication.getName());
    }
}
