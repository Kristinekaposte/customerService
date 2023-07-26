package com.customerService.busines.service.impl;

import com.customerService.busines.mappers.AddressMapper;
import com.customerService.busines.mappers.CustomerMapper;
import com.customerService.busines.repository.CustomerRepository;
import com.customerService.busines.repository.model.AddressDAO;
import com.customerService.busines.repository.model.CustomerDAO;
import com.customerService.busines.service.CustomerService;
import com.customerService.model.Address;
import com.customerService.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private AddressMapper addressMapper;
    public List<Customer> getAllCustomers() {
        List<Customer> list = customerRepository.findAll()
                .stream()
                .map(customerMapper::daoToCustomer)
                .collect(Collectors.toList());
        log.info("Size of the Customer list: {}", list.size());
        return list;
    }

    @Override
    public Optional<Customer> findCustomerById(Long id) {
        log.info("Looking for Customer entry with id " + id);
        Optional<CustomerDAO> customerDAO = customerRepository.findById(id);
        return Optional.ofNullable(customerDAO.map(customerMapper::daoToCustomer).orElse(null));
    }

    @Override
    @Transactional
    public Customer saveCustomer(Customer customer) {
        log.info("Saving customer details: " + customer);
        CustomerDAO newCustomerDAO = customerMapper.customerToDAO(customer);
        newCustomerDAO = customerRepository.save(newCustomerDAO);
        return customerMapper.daoToCustomer(newCustomerDAO);
    }

    /**
     * Method is running within a transaction.It ensures that the method is executed as a single unit,
     * and if any part of the method fails, the entire transaction is rolled back.
     *
     * Update is happening only in URL providing id,
     * addressDAO id in customerDAO table is ignored and will not be changed  because of OneToMany rel.
     * id - in customerDAO and addressDAO table are ignored to avoid mismatched id's is request body
     */
    @Override
    @Transactional
    public Customer editCustomerById(Long id, Customer updatedCustomer) {
        log.info("Updating customer details: " + updatedCustomer);
        CustomerDAO existingCustomerDAO = customerRepository.findById(id).orElse(null);
        BeanUtils.copyProperties(updatedCustomer, existingCustomerDAO, "id", "addressDAO");
        AddressDAO existingAddressDAO = existingCustomerDAO.getAddressDAO();
        AddressDAO updatedAddressDAO = addressMapper.addressToDAO(updatedCustomer.getAddress());
        BeanUtils.copyProperties(updatedAddressDAO, existingAddressDAO, "id");
        return customerMapper.daoToCustomer(existingCustomerDAO);
    }

    @Transactional
    @Override
    public Boolean deleteCustomerById(Long id) {
        log.info("Deleting Customer entry with id: {}", id);
        if (isCustomerPresent(id)) {
            customerRepository.deleteById(id);
            log.info("Customer entry with id: {} is deleted", id);
            return true;
        } else
            log.warn("Customer entry with id: {} does not exist", id);
        return false;
    }

    // method to check if the email is unique
    public boolean isEmailValid(String email) {
        return customerRepository.existsByEmail(email);
    }

    public boolean isCustomerPresent(Long id) {
        return customerRepository.existsById(id);
    }

}
