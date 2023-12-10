package com.nisolabluap.quickstart.application.controllers;

import com.nisolabluap.quickstart.application.models.dtos.OrderDTO;
import com.nisolabluap.quickstart.application.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO, @RequestParam Long customerId, @RequestParam List<Long> itemIds, @RequestParam List<Long> itemQuantities) {
        OrderDTO createdOrder = orderService.createOrder(orderDTO, customerId, itemIds, itemQuantities);
        return ResponseEntity.ok(createdOrder);
    }
}
