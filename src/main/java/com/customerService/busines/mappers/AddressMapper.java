package com.customerService.busines.mappers;

import com.customerService.busines.repository.model.AddressDAO;
import com.customerService.model.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressDAO addressToDAO (Address address);
    Address daoToAddress (AddressDAO addressDAO);
}
