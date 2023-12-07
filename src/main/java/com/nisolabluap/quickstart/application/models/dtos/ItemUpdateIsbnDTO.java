package com.nisolabluap.quickstart.application.models.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ItemUpdateIsbnDTO {

    @NotBlank(message = "ISBN must not be empty.")
    @Pattern(regexp = "^[0-9]{13}$", message = "ISBN must be a 13-digit number.")
    @Schema(description = "ISBN of the item", example = "1234567890123", pattern = "^[0-9]{13}$")
    private String isbn;
}
