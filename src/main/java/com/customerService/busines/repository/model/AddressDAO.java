package com.customerService.busines.repository.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "address")
public class AddressDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number", nullable = false, length = 11)
    private String phoneNumber;

    @Column(name = "country", nullable = false, length = 30)
    private String country;

    @Column(name = "city", nullable = false, length = 30)
    private String city;

    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;

    /**
     * Hibernate will figure out that it's
     * referenced to CustomerDAO table where @JoinColumn is
     * this makes bi-directional relationship for tables.
     */
//    @OneToOne(mappedBy = "addressDAO" )
//   private CustomerDAO customerDAO;
}
