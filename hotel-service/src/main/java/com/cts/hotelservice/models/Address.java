package com.cts.hotelservice.models;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Address {

    private String street;
    private String area;
    private String landmark;
    private String city;
    private String state;
    private String country;
    private String zipCode;
}
