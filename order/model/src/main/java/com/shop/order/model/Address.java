package com.shop.order.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Country name in address is mandatory!")
    @Size(min = 2, max = 32, message = "Country name for address must contains from 2 to 32 symbols!")
    @Column(name = "country", length = 32, nullable = false)
    private String country;

    @Size(min = 2, max = 32, message = "State name for address must contains from 2 to 32 symbols!")
    @Column(name = "state", length = 32)
    private String state;

    @Size(min = 2, max = 32, message = "City name for address must contains from 2 to 32 symbols!")
    @Column(name = "city", length = 32)
    private String city;

    @Size(min = 2, max = 64, message = "Street name for address must contains from 2 to 64 symbols!")
    @Column(name = "street", length = 64)
    private String street;

    @Size(min = 2, max = 16, message = "Hose number for address must contains from 2 to 16 symbols!")
    @Column(name = "house", length = 16)
    private String house;

    @Size(min = 2, max = 16, message = "Apartment number for address must contains from 2 to 16 symbols!")
    @Column(name = "apartment", length = 16)
    private String apartment;

}
