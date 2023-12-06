package com.nisolabluap.quickstart.application.models.dtos;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InventoryDTO {

    private Long id;

    @NotBlank(message = "Name must not be empty.")
    @Size(max = 50, message = "Name must not exceed 50 characters.")
    @ApiModelProperty(value = "Name must not be empty and must not exceed 50 characters.")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters.")
    @ApiModelProperty(value = "Description must not exceed 500 characters.")
    private String description;

    @NotBlank(message = "ISBN must not be empty.")
    @Pattern(regexp = "^[0-9]{13}$", message = "ISBN must be a 13-digit number.")
    @ApiModelProperty(value = "ISBN must not be empty and must be a 13-digit number.")
    private String isbn;

    @Min(value = 1, message = "Price must be greater than or equal to 0.")
    @ApiModelProperty(value = "Price must be greater than or equal to 0.")
    private double price;
}
