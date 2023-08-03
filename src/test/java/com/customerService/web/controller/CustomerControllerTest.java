package com.customerService.web.controller;

import com.customerService.business.repository.model.AddressDAO;
import com.customerService.business.repository.model.CustomerDAO;
import com.customerService.business.service.CustomerService;
import com.customerService.model.Address;
import com.customerService.model.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTest {
    @MockBean
    private WebRequest mockRequest;
    @MockBean
    private CustomerService customerService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    public static final String URL = "/api/v1/customer";
    public static final String URL1 = URL + "/allCustomers";
    public static final String URL2 = URL + "/getById";
    public static final String URL3 = URL + "/save";
    public static final String URL4 = URL + "/edit";
    public static final String URL5 = URL + "/delete";

    private List<Customer> customerList;
    private Customer customer;
    private Customer updatedCustomer;
    private Customer savedCustomer;

    @BeforeEach
    public void init() {
        customer = createCustomer();
        customerList = createCustomerList(customer);
        updatedCustomer = createUpdatedCustomer();
        savedCustomer = createCustomerToSave();
    }

    @Test
    void testGetAllCustomers_Successful() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(customerList);
        mockMvc.perform(get(URL1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(customerList.size())))
                .andExpect(jsonPath("$[0].id").value(customerList.get(0).getId()))
                .andExpect(jsonPath("$[0].firstName").value(customerList.get(0).getFirstName()))
                .andExpect(jsonPath("$[0].email").value(customerList.get(0).getEmail()))
                .andExpect(jsonPath("$[0].address.city").value(customerList.get(0).getAddress().getCity()));
        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    void testFindAllCustomers_WhenListEmpty_Successful() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(URL1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    void testGetCustomerById_ExistingId_Successful() throws Exception {
        when(customerService.findCustomerById(1L)).thenReturn(Optional.of(customer));
        mockMvc.perform(get(URL2 + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(customer.getId()))
                .andExpect(jsonPath("$.firstName").value(customer.getFirstName()))
                .andExpect(jsonPath("$.email").value(customer.getEmail()))
                .andExpect(jsonPath("$.address.city").value(customer.getAddress().getCity()));
        verify(customerService, times(1)).findCustomerById(1L);
    }

    @Test
    void testGetCustomerById_NonExistingId_UnSuccessful() throws Exception {
        when(customerService.findCustomerById(99L)).thenReturn(Optional.empty());
        mockMvc.perform(get(URL2 + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Message", "Customer not found with ID: " + 99));
        verify(customerService, times(1)).findCustomerById(99L);
    }

    @Test
    void testSaveCustomer_Successful() throws Exception {
        when(customerService.saveCustomer(any())).thenReturn(savedCustomer);
        mockMvc.perform(post(URL3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedCustomer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedCustomer.getId()))
                .andExpect(jsonPath("$.email").value(savedCustomer.getEmail()))
                .andExpect(jsonPath("$.password").value(savedCustomer.getPassword()))
                .andExpect(jsonPath("$.firstName").value(savedCustomer.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(savedCustomer.getLastName()))
                .andExpect(jsonPath("$.address.id").value(savedCustomer.getAddress().getId()))
                .andExpect(jsonPath("$.address.phoneNumber").value(savedCustomer.getAddress().getPhoneNumber()))
                .andExpect(jsonPath("$.address.city").value(savedCustomer.getAddress().getCity()))
                .andExpect(jsonPath("$.address.country").value(savedCustomer.getAddress().getCountry()))
                .andExpect(jsonPath("$.address.postalCode").value(savedCustomer.getAddress().getPostalCode()));
        verify(customerService, times(1)).saveCustomer(savedCustomer);
    }

    @Test
    void testSaveCustomer_ValidationFailure_Unsuccessful() throws Exception {
        Customer customerEmpty = new Customer();
        mockMvc.perform(
                        post(URL3)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerEmpty)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void testSaveCustomer_EmailExists_Unsuccessful() throws Exception {
        when(customerService.isEmailExisting(customer.getEmail())).thenReturn(true);
        mockMvc.perform(post(URL3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Sorry, the email " + customer.getEmail() + " is already registered.")));
        verify(customerService, times(1)).isEmailExisting(customer.getEmail());
        verify(customerService, times(0)).saveCustomer(any());
    }

    @Test
    void testEditCustomerById_Successful() throws Exception {
        when(customerService.isCustomerPresent(1L)).thenReturn(true);
        when(customerService.isEmailExisting(updatedCustomer.getEmail())).thenReturn(false);
        when(customerService.editCustomerById(eq(1L), any())).thenReturn(updatedCustomer);
        mockMvc.perform(put(URL4 + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedCustomer.getId()))
                .andExpect(jsonPath("$.email").value(updatedCustomer.getEmail()))
                .andExpect(jsonPath("$.password").value(updatedCustomer.getPassword()))
                .andExpect(jsonPath("$.firstName").value(updatedCustomer.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(updatedCustomer.getLastName()))
                .andExpect(jsonPath("$.address.id").value(updatedCustomer.getAddress().getId()))
                .andExpect(jsonPath("$.address.phoneNumber").value(updatedCustomer.getAddress().getPhoneNumber()))
                .andExpect(jsonPath("$.address.city").value(updatedCustomer.getAddress().getCity()))
                .andExpect(jsonPath("$.address.country").value(updatedCustomer.getAddress().getCountry()))
                .andExpect(jsonPath("$.address.postalCode").value(updatedCustomer.getAddress().getPostalCode()));
        verify(customerService, times(1)).isCustomerPresent(1L);
        verify(customerService, times(1)).isEmailExisting(updatedCustomer.getEmail());
        verify(customerService, times(1)).editCustomerById(eq(1L), any());
    }

    @Test
    void testEditCustomerById_EmailExists() throws Exception {
        when(customerService.isCustomerPresent(1L)).thenReturn(true);
        when(customerService.isEmailExisting(updatedCustomer.getEmail())).thenReturn(true);
        mockMvc.perform(put(URL4 + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCustomer)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Sorry, the email " + updatedCustomer.getEmail() + " is already registered.")));
        verify(customerService, times(1)).isCustomerPresent(1L);
        verify(customerService, times(1)).isEmailExisting(updatedCustomer.getEmail());
        verify(customerService, times(0)).editCustomerById(eq(1L), any());
    }

    @Test
    void testEditCustomerById_CustomerNotFound() throws Exception {
        when(customerService.isCustomerPresent(99L)).thenReturn(false);
        mockMvc.perform(put(URL4 + "/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCustomer)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Sorry, the customer id " + 99L + " does not exist.")));
        verify(customerService, times(1)).isCustomerPresent(99L);
        verify(customerService, times(0)).isEmailExisting(any());
        verify(customerService, times(0)).editCustomerById(any(), any());
    }

    @Test
    void testEditCustomerById_ValidationFailure() throws Exception {
        Customer invalidCustomer = new Customer();
        mockMvc.perform(put(URL4 + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCustomer)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Validation failed"));
        verify(customerService, times(0)).editCustomerById(anyLong(), any());
    }

    @Test
    void testDeleteCustomer_Successful() throws Exception {
        when(customerService.deleteCustomerById(1L)).thenReturn(true);
        mockMvc.perform(delete(URL5 + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Customer entry with ID " + 1L + " deleted")));
        verify(customerService, times(1)).deleteCustomerById(1L);
    }

    @Test
    void testDeleteCustomer_CustomerNotFound() throws Exception {
        when(customerService.deleteCustomerById(99L)).thenReturn(false);
        mockMvc.perform(delete(URL5 + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Customer not found with ID: " + 99L));
        verify(customerService, times(1)).deleteCustomerById(99L);
    }

    private Customer createCustomer() {
        return new Customer(1L, "email@email.com", "password1", "name1", "lastName1",
                new Address(1L, "12345678", "Riga", "Riga", "1001"));
    }

    private Customer createCustomerToSave() {
        return new Customer(null, "email@email.com", "password1", "name1", "lastName1",
                new Address(null, "12345678", "Riga", "Riga", "1001"));
    }

    private Customer createUpdatedCustomer() {
        return new Customer(1L, "updatedEmail2@email.com", "updatedPassword", "updatedName", "UpdatedLastName",
                new Address(1L, "464748494", "updated country", "updated city", "6666"));
    }

    private List<Customer> createCustomerList(Customer customer) {
        List<Customer> list = new ArrayList<>();
        list.add(customer);
        list.add(customer);
        return list;
    }
}
