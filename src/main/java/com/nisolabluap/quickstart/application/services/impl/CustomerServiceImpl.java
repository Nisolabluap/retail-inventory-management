package com.nisolabluap.quickstart.application.services.impl;

import com.nisolabluap.quickstart.application.exceptions.customer.CustomerNotFoundException;
import com.nisolabluap.quickstart.application.exceptions.customer.DuplicateEmailException;
import com.nisolabluap.quickstart.application.exceptions.item.ItemNotFoundException;
import com.nisolabluap.quickstart.application.models.dtos.CustomerDTO;
import com.nisolabluap.quickstart.application.models.dtos.ItemDTO;
import com.nisolabluap.quickstart.application.models.entities.Customer;
import com.nisolabluap.quickstart.application.models.entities.Item;
import com.nisolabluap.quickstart.application.repositories.CustomerRepository;
import com.nisolabluap.quickstart.application.repositories.ItemRepository;
import com.nisolabluap.quickstart.application.services.CustomerService;
import com.nisolabluap.quickstart.application.services.Validator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private final CustomerRepository customerRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final Validator validator;

    @Override
    public List<CustomerDTO> getAllCustomers() {
        List<Customer> inventories = customerRepository.findAll();
        return inventories.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        validateDuplicateItemByEmail(customerDTO.getEmail());
        Customer customer = convertToEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return convertToDTO(savedCustomer);
    }

    @Override
    public CustomerDTO updateCustomerById(Long id, CustomerDTO customerDTO) {
        validateDuplicateItemByEmail(customerDTO.getEmail());
        Customer existingCustomer = getCustomerByIdOrThrowException(id);
        BeanUtils.copyProperties(customerDTO, existingCustomer, "id", "createdAt");

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return convertToDTO(updatedCustomer);
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = getCustomerByIdOrThrowException(id);
        return convertToDTO(customer);
    }

    @Override
    public CustomerDTO deleteCustomerById(Long id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isEmpty()) {
            throw new CustomerNotFoundException(validator.getCustomerNotFoundMessage(id));
        }
        Customer deletedCustomer = optionalCustomer.get();
        customerRepository.deleteById(id);
        return convertToDTO(deletedCustomer);
    }

    @Override
    @Transactional
    public void toggleFavoriteItems(Long customerId, List<Long> itemIds) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(validator.getCustomerNotFoundMessage(customerId)));

        for (Long itemId : itemIds) {
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ItemNotFoundException(validator.getItemNotFoundMessage(itemId)));
            toggleFavoriteItem(customer, item);
        }
        customerRepository.save(customer);
    }

    private CustomerDTO convertToDTO(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);
        return customerDTO;
    }

    private Customer convertToEntity(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        return customer;
    }

    private void toggleFavoriteItem(Customer customer, Item item) {
        if (customer.getFavoriteItems().contains(item)) {
            customer.getFavoriteItems().remove(item);
        } else {
            customer.getFavoriteItems().add(item);
        }
    }

    private void validateDuplicateItemByEmail(String email) {
        if (customerRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(validator.getDuplicateEmailMessage(email));
        }
    }

    private Customer getCustomerByIdOrThrowException(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(validator.getCustomerNotFoundMessage(id)));
    }
}
