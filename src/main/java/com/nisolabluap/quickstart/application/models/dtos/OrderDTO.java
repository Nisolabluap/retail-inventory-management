package com.nisolabluap.quickstart.application.models.dtos;

import com.nisolabluap.quickstart.application.models.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {

    @Schema(description = "Order ID.", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "Customer ID is required.")
    @Schema(description = "Customer ID associated with the order.", example = "1")
    private Long customerId;

    @NotNull(message = "Order status is required.")
    @Schema(description = "Status of the order.", example = "PAYED")
    private OrderStatus orderStatus;

    @Schema(description = "Order creation date and time.", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "List of items with quantities and total price")
    private List<OrderItemDTO> items;

    private Integer totalQuantity;

    private double totalPrice;
}
