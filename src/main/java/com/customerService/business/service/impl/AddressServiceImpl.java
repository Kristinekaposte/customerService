package com.customerService.business.service.impl;

import com.customerService.business.repository.AddressRepository;
import com.customerService.business.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AddressServiceImpl implements AddressService {

    @Autowired
    AddressRepository addressRepository;


}
