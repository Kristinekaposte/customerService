package com.customerService.business.service.impl;

import com.customerService.business.mappers.AddressMapper;
import com.customerService.business.mappers.CustomerMapper;
import com.customerService.business.repository.CustomerRepository;
import com.customerService.business.repository.model.AddressDAO;
import com.customerService.business.repository.model.CustomerDAO;
import com.customerService.model.Address;
import com.customerService.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private AddressMapper addressMapper;
    @InjectMocks
    private CustomerServiceImpl customerService;

    private List<CustomerDAO> customerDAOList;
    private CustomerDAO customerDAO;
    private Customer customer;
    private Customer updatedCustomer;
    private AddressDAO addressDAO;
    private Address address;
    private Address updatedAddress;

    @BeforeEach
    public void init() {
        updatedAddress = createUpdatedAddress();
        address = createAddress();
        addressDAO = createAddressDAO();
        customerDAO = createCustomerDAO(addressDAO);
        customerDAOList = createCustomerDAOList(customerDAO);
        customer = createCustomer(address);
        updatedCustomer = createUpdatedCustomer(updatedAddress);
    }

    @Test
    public void testGetAllCustomerEntries_Successful() {
        when(customerRepository.findAll()).thenReturn(customerDAOList);
        when(customerMapper.daoToCustomer(customerDAO)).thenReturn(customer);
        List<Customer> list = customerService.getAllCustomers();
        assertEquals(2, list.size());
        assertEquals(customer.getId(), list.get(0).getId());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllCustomers_ListEmpty_Successful() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());
        List<Customer> result = customerService.getAllCustomers();
        verify(customerRepository, times(1)).findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void findCustomerById_Successful() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customerDAO));
        when(customerMapper.daoToCustomer(customerDAO)).thenReturn(customer);
        Optional<Customer> actualResult = customerService.findCustomerById(1L);
        assertTrue(actualResult.isPresent());
        assertEquals(customer, actualResult.get());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerMapper, times(1)).daoToCustomer(customerDAO);
    }

    @Test
    void testFindCustomerById_NonExistingId_Failed() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Customer> result = customerService.findCustomerById(99L);
        assertFalse(result.isPresent());
        verify(customerRepository, times(1)).findById(anyLong());
    }

    @Test
    void testSaveCustomer_Successful() {
        when(customerMapper.customerToDAO(customer)).thenReturn(customerDAO);
        when(customerRepository.save(customerDAO)).thenReturn(customerDAO);
        when(customerMapper.daoToCustomer(customerDAO)).thenReturn(customer);
        Customer savedCustomer = customerService.saveCustomer(customer);
        assertNotNull(savedCustomer);
        assertEquals(customer, savedCustomer);
        verify(customerMapper, times(1)).customerToDAO(customer);
        verify(customerRepository, times(1)).save(customerDAO);
        verify(customerMapper, times(1)).daoToCustomer(customerDAO);
    }

    @Test
    void testSaveCustomer_Unsuccessful() {
        when(customerMapper.customerToDAO(customer)).thenReturn(customerDAO);
        when(customerRepository.save(customerDAO)).thenReturn(null);
        Customer savedCustomer = customerService.saveCustomer(customer);
        assertNull(savedCustomer);
        verify(customerMapper, times(1)).customerToDAO(customer);
        verify(customerRepository, times(1)).save(customerDAO);
        verify(customerMapper, times(0)).daoToCustomer(any());
    }


    @Test
    void testEditCustomerById_CustomerFound_Successful() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customerDAO));

        // Mock addressMapper.addressToDAO
        when(addressMapper.addressToDAO(updatedAddress)).thenReturn(new AddressDAO());

        // Mock customerRepository.save
        when(customerRepository.save(any(CustomerDAO.class))).thenReturn(customerDAO);

        // Mock customerMapper.daoToCustomer
        when(customerMapper.daoToCustomer(customerDAO)).thenReturn(updatedCustomer);

        // Call the method under test
        Customer result = customerService.editCustomerById(1L, updatedCustomer);

        // Verify the results
        assertNotNull(result);
        assertEquals(updatedCustomer, result);
        // Verify that the repository methods were called
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(any(CustomerDAO.class));
        verify(addressMapper, times(1)).addressToDAO(updatedAddress);
        verify(customerMapper, times(1)).daoToCustomer(customerDAO);
    }

    @Test
    void testEditCustomerById_CustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        Customer result = customerService.editCustomerById(1L, updatedCustomer);
        assertNull(result);
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    public void testDeleteCustomerById_ExistingCustomer_Successful() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        boolean isDeleted = customerService.deleteCustomerById(1L);
        assertTrue(isDeleted);
        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteCustomerById_NonExistingCustomer_Unsuccessful() {
        when(customerRepository.existsById(99L)).thenReturn(false);
        boolean isDeleted = customerService.deleteCustomerById(99L);
        assertFalse(isDeleted);
        verify(customerRepository, times(0)).deleteById(99L);
    }

    @Test
    public void testIsEmailExisting_EmailExists() {
        when(customerRepository.existsByEmail("existingEmail@email.com")).thenReturn(true);
        boolean result = customerService.isEmailExisting("existingEmail@email.com");
        assertTrue(result);
    }

    @Test
    public void testIsEmailExisting_EmailDoesNotExist() {
        when(customerRepository.existsByEmail("nonexisting@email.com")).thenReturn(false);
        boolean result = customerService.isEmailExisting("nonexisting@email.com");
        assertFalse(result);
    }

    private CustomerDAO createCustomerDAO(AddressDAO addressDAO) {
        return new CustomerDAO(1L, "email@email.com", "password1", "name1", "lastName1", addressDAO);
    }

    private AddressDAO createAddressDAO() {
        return new AddressDAO(1L, "12345678", "Riga", "Riga", "1001");
    }

    private Customer createCustomer(Address address) {
        return new Customer(1L, "email@email.com", "password1", "name1", "lastName1", address);
    }

    private Address createAddress() {
        return new Address(1L, "12345678", "Riga", "Riga", "1001");
    }

    private Customer createUpdatedCustomer(Address updatedAddress) {
        return new Customer(1L, "updatedEmail2@email.com", "updatedPassword", "updatedName", "UpdatedLastName", updatedAddress);
    }

    private Address createUpdatedAddress() {
        return new Address(1L, "464748494", "updated country", "updated city", "6666");
    }

    private List<CustomerDAO> createCustomerDAOList(CustomerDAO customerDAO) {
        List<CustomerDAO> list = new ArrayList<>();
        list.add(customerDAO);
        list.add(customerDAO);
        return list;
    }


}
