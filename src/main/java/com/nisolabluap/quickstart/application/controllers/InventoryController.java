package com.nisolabluap.quickstart.application.controllers;

import com.nisolabluap.quickstart.application.models.dtos.InventoryDTO;
import com.nisolabluap.quickstart.application.services.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory API")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    @Operation(
            summary = "Get all inventory items.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inventory items retrieved successfully."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public List<InventoryDTO> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    @PostMapping
    @Operation(
            summary = "Create a new inventory item.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inventory item created successfully."),
                    @ApiResponse(responseCode = "400", description = "Bad Request."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public InventoryDTO createInventoryItem(@Valid @RequestBody InventoryDTO inventoryDTO) {
        return inventoryService.createInventoryItem(inventoryDTO);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an inventory item by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inventory item updated successfully."),
                    @ApiResponse(responseCode = "400", description = "Bad Request."),
                    @ApiResponse(responseCode = "404", description = "Inventory item not found."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public InventoryDTO updateInventoryItem(@PathVariable Long id, @Valid @RequestBody InventoryDTO inventoryDTO) {
        return inventoryService.updateInventoryItem(id, inventoryDTO);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get an inventory item by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inventory item retrieved successfully."),
                    @ApiResponse(responseCode = "404", description = "Inventory item not found."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public InventoryDTO getInventoryItem(@PathVariable Long id) {
        return inventoryService.getInventoryByItemId(id);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete an inventory item by ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Inventory item deleted successfully."),
                    @ApiResponse(responseCode = "404", description = "Inventory item not found."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public ResponseEntity<Void> deleteInventoryItem(@PathVariable Long id) {
        inventoryService.deleteInventoryItem(id);
        return ResponseEntity.noContent().build();
    }

    @Value("${inventory.delete.password}")
    private String deletePassword;

    @DeleteMapping("/delete-all")
    @Operation(
            summary = "Delete all inventory items.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "All inventory items have been deleted."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.")
            }
    )
    public ResponseEntity<String> deleteAllInventory(@RequestParam String password) {
        if (deletePassword.equals(password)) {
            inventoryService.deleteAllInventory();
            return ResponseEntity.ok("All inventory items have been deleted.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong password.");
        }
    }
}
