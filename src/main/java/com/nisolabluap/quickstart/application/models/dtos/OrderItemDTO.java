package com.nisolabluap.quickstart.application.models.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrderItemDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long itemId;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String itemName;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer quantityPerItem;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private double price;
}
