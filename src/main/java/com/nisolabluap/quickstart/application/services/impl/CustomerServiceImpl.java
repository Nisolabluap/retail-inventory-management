package com.nisolabluap.quickstart.application.services.impl;

import com.nisolabluap.quickstart.application.exceptions.customer.CustomerNotFoundException;
import com.nisolabluap.quickstart.application.exceptions.customer.DuplicateEmailException;
import com.nisolabluap.quickstart.application.exceptions.item.ItemNotFoundException;
import com.nisolabluap.quickstart.application.models.dtos.CustomerCreateDTO;
import com.nisolabluap.quickstart.application.models.dtos.CustomerDTO;
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
    private CustomerRepository customerRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private Validator validator;

    @Override
    public List<CustomerDTO> getAllCustomers() {
        List<Customer> inventories = customerRepository.findAll();
        return inventories.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public CustomerDTO createCustomer(CustomerCreateDTO customerCreateDTO) {
        validateDuplicateEmail(customerCreateDTO.getEmail());
        Customer customer = convertToEntity(customerCreateDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return convertToDTO(savedCustomer);
    }

    @Override
    public CustomerDTO updateCustomerById(Long id, CustomerCreateDTO customerCreateDTO) {
        Customer existingCustomer = getCustomerByIdOrThrowException(id);
        validateDuplicateIdAndEmail(id, customerCreateDTO.getEmail());
        BeanUtils.copyProperties(customerCreateDTO, existingCustomer, "id", "createdAt");

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return convertToDTO(updatedCustomer);
    }

    @Transactional
    @Override
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = getCustomerByIdOrThrowException(id);
        return convertToDTO(customer);
    }

    @Override
    public void deleteCustomerById(Long id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isEmpty()) {
            throw new CustomerNotFoundException(validator.getCustomerNotFoundMessage(id));
        }
        Customer deletedCustomer = optionalCustomer.get();
        customerRepository.deleteById(id);
        convertToDTO(deletedCustomer);
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

    private Customer convertToEntity(CustomerCreateDTO customerCreateDTO) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerCreateDTO, customer);
        return customer;
    }

    private void toggleFavoriteItem(Customer customer, Item item) {
        if (customer.getFavoriteItems().contains(item)) {
            customer.getFavoriteItems().remove(item);
        } else {
            customer.getFavoriteItems().add(item);
        }
    }

    private void validateDuplicateIdAndEmail(Long userId, String email) {
        if (customerRepository.existsByEmailAndIdNot(email, userId)) {
            throw new DuplicateEmailException(validator.getDuplicateEmailMessage(email));
        }
    }

    private void validateDuplicateEmail(String email) {
        if (customerRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(validator.getDuplicateEmailMessage(email));
        }
    }

    private Customer getCustomerByIdOrThrowException(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(validator.getCustomerNotFoundMessage(id)));
    }
}
