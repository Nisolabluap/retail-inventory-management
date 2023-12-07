package com.nisolabluap.quickstart.application.services;

import com.nisolabluap.quickstart.application.models.dtos.CustomerDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CustomerService {

    List<CustomerDTO> getAllCustomers();

    CustomerDTO createCustomer(CustomerDTO customerDTO);

    CustomerDTO updateCustomerById(Long id, CustomerDTO customerDTO);

    CustomerDTO getCustomerById(Long id);

    CustomerDTO deleteCustomerById(Long id);

    @Transactional
    void toggleFavoriteItems(Long customerId, List<Long> itemIds);
}
