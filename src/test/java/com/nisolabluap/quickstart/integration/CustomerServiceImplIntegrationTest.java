package com.nisolabluap.quickstart.integration;

import com.nisolabluap.quickstart.application.models.entities.Customer;
import com.nisolabluap.quickstart.application.repositories.CustomerRepository;
import com.nisolabluap.quickstart.application.services.impl.CustomerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplIntegrationTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void deleteCustomer_ShouldPass() {
        // Given
        Long customerIdToDelete = 1L;

        when(customerRepository.findById(customerIdToDelete)).thenReturn(Optional.of(new Customer()));
        doNothing().when(customerRepository).deleteById(customerIdToDelete);

        // When and Then
        assertDoesNotThrow(() -> customerService.deleteCustomerById(customerIdToDelete));

        // Verify that findById was called with the correct ID
        verify(customerRepository).findById(customerIdToDelete);

        // Verify that deleteById was called with the correct ID
        verify(customerRepository).deleteById(customerIdToDelete);
    }
}
