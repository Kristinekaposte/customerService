package com.customerService.web.controller;

import com.customerService.business.service.CustomerService;
import com.customerService.model.Customer;
import com.customerService.swagger.DescriptionVariables;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Api(tags = DescriptionVariables.CUSTOMER)
@Slf4j
@AllArgsConstructor
@RequestMapping("api/v1/customer")
@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/allCustomers")
    @ApiOperation(value = "Finds all Customer entries",
            notes = "Returns all Customer entries from the database",
            response = Customer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<List<Customer>> getAllCustomerEntries() {
        List<Customer> list = customerService.getAllCustomers();
        if (list.isEmpty()) {
            log.info("Empty customer list found");
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        log.info("List size: {}", list.size());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/getById/{id}")
    @ApiOperation(value = "Find a Customer by ID",
            notes = "Returns a single Customer entry based on the provided ID",
            response = Customer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")})

    public ResponseEntity<Customer> getInternStaffingById(@ApiParam(value = "id of the Customer entry", required = true)
                                                          @PathVariable("id") Long id) {
        Optional<Customer> customerOptional = customerService.findCustomerById(id);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            log.info("Found Customer with ID {}: {}", id, customer);
            return ResponseEntity.status(HttpStatus.OK).body(customer);
        }
        log.warn("Customer not found with ID: {}", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                "Message", "Customer not found with ID: " + id).build();
    }

    @PostMapping("/save")
    @ApiOperation(value = "Saves Customer entry in database",
            notes = "Provide customer data to save.",
            response = Customer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The request has create successfully"),
            @ApiResponse(code = 400, message = "The server has Bad Request, cannot process due invalid request"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<?> saveCustomer(@RequestBody @Valid Customer customer) {
        String email = customer.getEmail();
        if (customerService.isEmailExisting(email)) {
            log.info("The email " + email +" is already registered");
            return new ResponseEntity<>("Sorry, the email " + email + " is already registered.", HttpStatus.BAD_REQUEST);
        }
        Customer savedCustomer = customerService.saveCustomer(customer);
        log.info("Customer entry saved: {}", savedCustomer);
        return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
    }

    @PutMapping("/edit/{id}")
    @ApiOperation(value = "Edits Customer entry by ID",
            notes = "Provide an id to edit specific customer in the database",
            response = Customer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 400, message = "The server has Bad Request, cannot process due to an invalid request"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")
    })
    public ResponseEntity<?> editCustomerById(@PathVariable Long id, @RequestBody @Valid Customer updatedCustomer) {
        if (!customerService.isCustomerPresent(id)) {
            log.warn("Sorry, the customer with id " + id + " does not exist.");
            return new ResponseEntity<>("Sorry, the customer id " + id + " does not exist.", HttpStatus.NOT_FOUND);
        }
        String email = updatedCustomer.getEmail();
        if (customerService.isEmailExisting(email)) {
            log.info("Customer with email {} is already registered.", email);
            return new ResponseEntity<>("Sorry, the email " + email + " is already registered.", HttpStatus.BAD_REQUEST);
        }
        Customer editedCustomer = customerService.editCustomerById(id, updatedCustomer);
        log.info("Customer with ID {} updated successfully.", id);
        return ResponseEntity.ok(editedCustomer);
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "Deletes Customer entry by ID",
            notes = "Provide an id to delete specific customer from the database",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")
    })
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        Boolean isDeleteSuccessful = customerService.deleteCustomerById(id);
        if (isDeleteSuccessful) {
            log.info("Customer entry with ID: {} deleted", id);
            return ResponseEntity.ok("Customer entry with ID " + id + " deleted");
        }
        log.warn("Cannot delete Customer entry with ID: {}, customer not found", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found with ID: " + id);
    }

}
