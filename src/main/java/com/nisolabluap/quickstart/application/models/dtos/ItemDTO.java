package com.nisolabluap.quickstart.application.models.dtos;

import com.nisolabluap.quickstart.application.models.enums.ProductCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ItemDTO {

    @Schema(description = "Item ID.", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Name must not be blank.")
    @Size(max = 50, message = "Name must not exceed 50 characters.")
    @Schema(description = "Name of the item.", example = "Sample Item")
    private String name;

    @NotBlank(message = "Description must not be blank.")
    @Size(max = 500, message = "Description must not exceed 500 characters.")
    @Schema(description = "Description of the item.", example = "This is a sample item.", maxLength = 500)
    private String description;

    @NotNull(message = "Quantity must not be null.")
    @Schema(description = "Available quantity of the item.", example = "100")
    private Long availableQuantity;

    @Schema(description = "Product category of the item.", example = "GARDENING", accessMode = Schema.AccessMode.READ_ONLY)
    private ProductCategory productCategory;

    @NotNull(message = "Price must not be null.")
    @Min(value = 0, message = "Price must be greater than or equal to 0.")
    @Schema(description = "Price of the item.", example = "49.99", minimum = "0.0")
    private Double price;

    @Schema(description = "ISBN of the item.", accessMode = Schema.AccessMode.READ_ONLY)
    private String isbn;
}
