package com.nisolabluap.quickstart.application.controllers;

import com.nisolabluap.quickstart.application.models.dtos.ItemDTO;
import com.nisolabluap.quickstart.application.models.dtos.ItemUpdateIsbnDTO;
import com.nisolabluap.quickstart.application.models.enums.ProductCategory;
import com.nisolabluap.quickstart.application.services.ItemService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/items")
@Tag(name = "Item API")
public class ItemController {

    @Autowired
    private final ItemService itemService;

    @GetMapping("/search")
    @Operation(
            summary = "Search items based on parameters.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Items retrieved successfully."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public ResponseEntity<List<ItemDTO>> searchItems(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "category", required = false) ProductCategory category,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "isbn", required = false) String isbn,
            @RequestParam(value = "quantity", required = false) Long availableQuantity,
            @RequestParam(value = "price", required = false) Double price) {
        List<ItemDTO> items = itemService.searchItems(name, category, description, id, isbn, availableQuantity, price);
        return ResponseEntity.ok(items);
    }

    @GetMapping
    @Operation(
            summary = "Get all items.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Items retrieved successfully."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public List<ItemDTO> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems()).getBody();
    }

    @PostMapping
    @Operation(
            summary = "Create a new item.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Item created successfully."),
                    @ApiResponse(responseCode = "400", description = "Bad Request."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public ItemDTO createItem(@Valid @RequestBody ItemDTO itemDTO, @RequestParam ProductCategory productCategory) {
        return ResponseEntity.ok(itemService.createItem(itemDTO, productCategory)).getBody();
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an item by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Item updated successfully."),
                    @ApiResponse(responseCode = "400", description = "Bad Request."),
                    @ApiResponse(responseCode = "404", description = "Item not found."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public ItemDTO updateItemById(@PathVariable Long id, @Valid @RequestBody ItemDTO itemDTO, @RequestParam ProductCategory productCategory) {
        return ResponseEntity.ok(itemService.updateItemById(id, itemDTO, productCategory)).getBody();
    }

    @PutMapping("/{id}/{isbn}")
    @Operation(
            summary = "Update an item isbn by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Item updated successfully."),
                    @ApiResponse(responseCode = "400", description = "Bad Request."),
                    @ApiResponse(responseCode = "404", description = "Item not found."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public ItemUpdateIsbnDTO updateItemIsbnById(@PathVariable Long id, @Valid @RequestBody ItemUpdateIsbnDTO itemUpdateIsbnDTO) {
        return ResponseEntity.ok(itemService.updateItemIsbnById(id, itemUpdateIsbnDTO)).getBody();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get an item by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Item retrieved successfully."),
                    @ApiResponse(responseCode = "404", description = "Item not found."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public ItemDTO getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete an item by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Item deleted successfully."),
                    @ApiResponse(responseCode = "404", description = "Item not found."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public ResponseEntity<String> deleteItemById(@PathVariable Long id) {
        itemService.deleteItemById(id);
        return ResponseEntity.ok("The item has been deleted.");
    }


}
