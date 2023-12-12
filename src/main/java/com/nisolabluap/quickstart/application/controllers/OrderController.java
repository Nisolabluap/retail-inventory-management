package com.nisolabluap.quickstart.application.controllers;

import com.nisolabluap.quickstart.application.models.dtos.OrderDTO;
import com.nisolabluap.quickstart.application.models.enums.OrderStatus;
import com.nisolabluap.quickstart.application.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(
            summary = "Create a new order.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order created successfully."),
                    @ApiResponse(responseCode = "400", description = "Bad Request."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO, @RequestParam Long customerId, @RequestParam List<Long> itemIds, @RequestParam List<Long> itemQuantities) {
        OrderDTO createdOrder = orderService.createOrder(orderDTO, customerId, itemIds, itemQuantities);
        return ResponseEntity.ok(createdOrder);
    }

    @PutMapping("/{orderId}/status")
    @Operation(
            summary = "Update order status by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order status updated successfully."),
                    @ApiResponse(responseCode = "400", description = "Bad Request."),
                    @ApiResponse(responseCode = "404", description = "Order not found."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus newStatus) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, newStatus);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    @Operation(
            summary = "Get an order by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order retrieved successfully."),
                    @ApiResponse(responseCode = "404", description = "Order not found."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public ResponseEntity<OrderDTO> findOrderById(@PathVariable Long orderId) {
        OrderDTO foundOrder = orderService.findOrderById(orderId);
        return new ResponseEntity<>(foundOrder, HttpStatus.OK);
    }
}
