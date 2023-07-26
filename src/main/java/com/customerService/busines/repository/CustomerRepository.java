package com.customerService.busines.repository;

import com.customerService.busines.repository.model.CustomerDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerDAO,Long> {

    boolean existsByEmail(String email);

}
