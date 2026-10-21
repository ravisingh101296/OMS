package com.example.order_management.controller;

import com.example.order_management.dto.CreateOrderRequest;
import com.example.order_management.dto.CreateOrderResponse;
import com.example.order_management.dto.OrderResponse;
import com.example.order_management.entity.OrderEntity;
import com.example.order_management.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable UUID id) {
        return orderService.getOrderDetailsById(id);
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(
            @RequestHeader("Idempotency-Key") String IdempotenckKey,
            @Valid @RequestBody CreateOrderRequest request){
        OrderEntity order = orderService.createOrder(request,IdempotenckKey);
        CreateOrderResponse response = new CreateOrderResponse(order.getId(),order.getStatus(),order.getTotalAmount(),order.getCreatedAt());

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(order.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(response);
    }
}
