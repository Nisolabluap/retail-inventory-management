package com.nisolabluap.quickstart.application.models.dtos;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerCreateDTO {

    @Schema(description = "Customer ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "First name is required.")
    @Size(max = 25, message = "Name must not exceed 50 characters.")
    @ApiModelProperty(value = "Name must not be empty and must not exceed 50 characters.")
    @Schema(example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(max = 25, message = "Name must not exceed 50 characters.")
    @ApiModelProperty(value = "Name must not be empty and must not exceed 50 characters.")
    @Schema(example = "Doe")
    private String lastName;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9.]+@[A-Za-z]+\\.[A-Za-z]{2,}$", message = "Invalid email address.")
    @ApiModelProperty(value = "Email should be in this format abc@cba.com")
    @Schema(example = "john.doe@example.com")
    private String email;

    @Schema(example = "2023-12-07")
    private LocalDate birthday;

    @NotBlank(message = "Address is required.")
    @Size(max = 50, message = "Address must not exceed 50 characters.")
    @ApiModelProperty(value = "Address must not be empty and must not exceed 50 characters.")
    @Schema(example = "123 Main St")
    private String address;
}