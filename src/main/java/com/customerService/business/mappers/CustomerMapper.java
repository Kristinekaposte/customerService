package com.customerService.business.mappers;

import com.customerService.business.repository.model.CustomerDAO;
import com.customerService.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface CustomerMapper {

    @Mapping(source = "address", target = "addressDAO")
    CustomerDAO customerToDAO(Customer customer);

    @Mapping(source = "addressDAO", target = "address")
    Customer daoToCustomer(CustomerDAO customerDAO);
}
