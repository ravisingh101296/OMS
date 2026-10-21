package com.example.order_management.service;

import com.example.order_management.dto.CreateOrderRequest;
import com.example.order_management.entity.*;
import com.example.order_management.exception.InsufficientStockException;
import com.example.order_management.repository.IdempotencyRecordRepository;
import com.example.order_management.repository.OrderRepository;
import com.example.order_management.repository.ProductRepository;
import com.example.order_management.repository.UserRepository;
import com.example.order_management.util.RequestHashUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderCreationTransactionService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final UserRepository userRepository;

    public OrderCreationTransactionService(OrderRepository orderRepository, ProductRepository productRepository, IdempotencyRecordRepository idempotencyRecordRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.idempotencyRecordRepository = idempotencyRecordRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderEntity createOrder(
            CreateOrderRequest request,
            String idempotencyKey,
            UUID userId
    ) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user no longer exists"
                ));

        String requestHash = RequestHashUtil.hash(request);

        var existingRecord = idempotencyRecordRepository
                .findByIdempotencyKey(idempotencyKey);

        if (existingRecord.isPresent()) {
            var record = existingRecord.get();

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

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        BigDecimal total = BigDecimal.ZERO;

        Map<UUID, Integer> quantitiesByProduct = new TreeMap<>();

        for (var item : request.items()) {
            quantitiesByProduct.merge(
                    item.id(),
                    item.quantity(),
                    Math::addExact
            );
        }

        List<ProductEntity> lockedProducts =
                productRepository.findALLByIdForUpdate(
                        quantitiesByProduct.keySet()
                );

        Map<UUID, ProductEntity> productByIds = lockedProducts.stream()
                .collect(Collectors.toMap(
                        ProductEntity::getId,
                        Function.identity()
                ));

        for (var entry : quantitiesByProduct.entrySet()) {
            UUID productId = entry.getKey();
            int quantity = entry.getValue();

            ProductEntity product = productByIds.get(productId);

            if (product == null) {
                throw new com.example.order_management.exception.ProductNotFoundException(
                        productId
                );
            }

            if (product.getStock() < quantity) {
                throw new InsufficientStockException(product.getName());
            }

            product.decreaseStock(quantity);

            BigDecimal unitPrice = product.getPrice();

            OrderItemEntity item = new OrderItemEntity(
                    order, product, quantity, unitPrice
            );

            order.addItem(item);

            total = total.add(
                    unitPrice.multiply(BigDecimal.valueOf(quantity))
            );
        }

        order.setTotalAmount(total);

        OrderEntity savedOrder = orderRepository.save(order);

        idempotencyRecordRepository.saveAndFlush(
                new IdempotencyRecord(
                        idempotencyKey,
                        savedOrder.getId(),
                        requestHash
                )
        );

        return savedOrder;
    }

}
