package com.nisolabluap.quickstart.application.controllers;

import com.nisolabluap.quickstart.application.models.dtos.OrderDTO;
import com.nisolabluap.quickstart.application.models.enums.OrderStatus;
import com.nisolabluap.quickstart.application.services.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/orders")
@Tag(name = "Order API")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO, @RequestParam Long customerId, @RequestParam List<Long> itemIds, @RequestParam List<Long> itemQuantities) {
        OrderDTO createdOrder = orderService.createOrder(orderDTO, customerId, itemIds, itemQuantities);
        return ResponseEntity.ok(createdOrder);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus newStatus) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, newStatus);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> findOrderById(@PathVariable Long orderId) {
        OrderDTO foundOrder = orderService.findOrderById(orderId);
        return new ResponseEntity<>(foundOrder, HttpStatus.OK);
    }
}
