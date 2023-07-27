package com.customerService.business.service;

import com.customerService.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<Customer> getAllCustomers();

    Optional<Customer> findCustomerById(Long id);

    Customer saveCustomer(Customer customer);

    Customer editCustomerById(Long id, Customer updatedCustomer);

    Boolean deleteCustomerById(Long id);

    boolean isEmailExisting(String email);
    boolean isCustomerPresent(Long id);
}
