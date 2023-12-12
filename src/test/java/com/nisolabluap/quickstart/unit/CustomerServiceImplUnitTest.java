package com.nisolabluap.quickstart.unit;

import com.nisolabluap.quickstart.application.models.dtos.CustomerCreateDTO;
import com.nisolabluap.quickstart.application.models.dtos.CustomerDTO;
import com.nisolabluap.quickstart.application.models.entities.Customer;
import com.nisolabluap.quickstart.application.repositories.CustomerRepository;
import com.nisolabluap.quickstart.application.repositories.ItemRepository;
import com.nisolabluap.quickstart.application.services.Validator;
import com.nisolabluap.quickstart.application.services.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplUnitTest {

    private CustomerRepository customerRepository;
    private ItemRepository itemRepository;
    private Validator validator;
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        itemRepository = mock(ItemRepository.class);
        validator = mock(Validator.class);
        customerService = new CustomerServiceImpl(customerRepository, itemRepository, validator);
    }

    @Test
    void createCustomer_ShouldPass() {
        // Given
        CustomerCreateDTO inputCustomerCreateDTO = new CustomerCreateDTO();
        inputCustomerCreateDTO.setEmail("john.doe@example.com");
        inputCustomerCreateDTO.setFirstName("John");
        inputCustomerCreateDTO.setLastName("Doe");

        Customer inputCustomer = new Customer();
        inputCustomer.setEmail("john.doe@example.com");
        inputCustomer.setFirstName("John");
        inputCustomer.setLastName("Doe");

        Customer savedCustomer = new Customer();
        savedCustomer.setEmail("john.doe@example.com");
        savedCustomer.setFirstName("John");
        savedCustomer.setLastName("Doe");
        savedCustomer.setId(1L);

        // When
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.save(any())).thenReturn(savedCustomer);
        lenient().when(validator.getDuplicateEmailMessage(anyString())).thenReturn("Duplicate email message");

        // Then
        CustomerDTO resultCustomerDTO = customerService.createCustomer(inputCustomerCreateDTO);

        // Assert
        assertNotNull(resultCustomerDTO);
        assertEquals(savedCustomer.getId(), resultCustomerDTO.getId());
        assertEquals(savedCustomer.getEmail(), resultCustomerDTO.getEmail());
        assertEquals(savedCustomer.getFirstName(), resultCustomerDTO.getFirstName());
        assertEquals(savedCustomer.getLastName(), resultCustomerDTO.getLastName());
    }
}
