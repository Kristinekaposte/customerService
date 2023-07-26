package com.customerService.busines.repository;

import com.customerService.busines.repository.model.AddressDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<AddressDAO,Long> {

}
