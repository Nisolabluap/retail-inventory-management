package com.nisolabluap.quickstart.application.services;

import com.nisolabluap.quickstart.application.models.dtos.CustomerCreateDTO;
import com.nisolabluap.quickstart.application.models.dtos.CustomerDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CustomerService {

    List<CustomerDTO> getAllCustomers();

    CustomerDTO createCustomer(CustomerCreateDTO customerCreateDTO);

    CustomerDTO updateCustomerById(Long id, CustomerCreateDTO customerCreateDTO);

    CustomerDTO getCustomerById(Long id);

    void deleteCustomerById(Long id);

    @Transactional
    void toggleFavoriteItems(Long customerId, List<Long> itemIds);
}
