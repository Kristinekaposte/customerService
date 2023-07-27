package com.customerService.business.repository;

import com.customerService.business.repository.model.AddressDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<AddressDAO,Long> {

}
