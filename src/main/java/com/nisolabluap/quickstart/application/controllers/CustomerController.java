package com.nisolabluap.quickstart.application.controllers;

import com.nisolabluap.quickstart.application.models.dtos.CustomerCreateDTO;
import com.nisolabluap.quickstart.application.models.dtos.CustomerDTO;
import com.nisolabluap.quickstart.application.models.dtos.ItemDTO;
import com.nisolabluap.quickstart.application.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/customers")
@Tag(name = "Customer API")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    @Operation(
            summary = "Get all customers.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customers retrieved successfully."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public List<CustomerDTO> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers()).getBody();
    }

    @PostMapping("/customers")
    @Operation(
            summary = "Create a new customer.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customer created successfully."),
                    @ApiResponse(responseCode = "400", description = "Bad Request."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public ResponseEntity<CustomerCreateDTO> createCustomer(@Valid @RequestBody CustomerCreateDTO customerCreateDTO) {
        CustomerDTO createdCustomer = customerService.createCustomer(customerCreateDTO);
        CustomerCreateDTO createdCustomerCreateDTO = convertToCustomerCreateDTO(createdCustomer);
        return ResponseEntity.ok(createdCustomerCreateDTO);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a customer by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customer updated successfully."),
                    @ApiResponse(responseCode = "400", description = "Bad Request."),
                    @ApiResponse(responseCode = "404", description = "Customer not found."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public ResponseEntity<CustomerDTO> updateCustomerById(@PathVariable Long id, @Valid @RequestBody CustomerCreateDTO customerCreateDTO) {
        CustomerDTO updatedCustomer = customerService.updateCustomerById(id, customerCreateDTO);
        return ResponseEntity.ok(updatedCustomer);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a customer by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customer retrieved successfully."),
                    @ApiResponse(responseCode = "404", description = "Customer not found."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public CustomerDTO getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a customer by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customer deleted successfully."),
                    @ApiResponse(responseCode = "404", description = "Customer not found."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public ResponseEntity<String> deleteCustomerById(@PathVariable Long id) {
        customerService.deleteCustomerById(id);
        return ResponseEntity.ok("The customer has been deleted.");
    }

    @PostMapping("/{customerId}/favorites")
    @Operation(
            summary = "Toggle the status of multiple favorite items for a customer.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Toggle successful."),
                    @ApiResponse(responseCode = "404", description = "Customer or item not found."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public ResponseEntity<String> toggleFavoriteItems(
            @PathVariable Long customerId,
            @RequestParam List<Long> itemIds) {
        customerService.toggleFavoriteItems(customerId, itemIds);
        return ResponseEntity.ok("Action complete.");
    }

    private CustomerCreateDTO convertToCustomerCreateDTO(CustomerDTO customerDTO) {
        CustomerCreateDTO customerCreateDTO = new CustomerCreateDTO();
        BeanUtils.copyProperties(customerDTO, customerCreateDTO);
        return customerCreateDTO;
    }
}
