package com.nisolabluap.quickstart.application.models.dtos;

import com.nisolabluap.quickstart.application.enums.ProductCategory;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Name must not be empty.")
    @Size(max = 50, message = "Name must not exceed 50 characters.")
    @ApiModelProperty(value = "Name must not be empty and must not exceed 50 characters.")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters.")
    @ApiModelProperty(value = "Description must not exceed 500 characters.")
    private String description;

    private Long availableQuantity;

    @NotNull(message = "Product category must not be empty.")
    @ApiModelProperty(value = "Product category must not be empty.")
    private ProductCategory productCategory;

    @Min(value = 1, message = "Price must be greater than or equal to 0.")
    @ApiModelProperty(value = "Price must be greater than or equal to 0.")
    private double price;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String isbn;
}
