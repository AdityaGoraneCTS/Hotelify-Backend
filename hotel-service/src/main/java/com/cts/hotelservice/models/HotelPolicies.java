package com.cts.hotelservice.models;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class HotelPolicies {

    private String checkInTime;
    private String checkOutTime;
    private String cancellationPolicy;
    private boolean smokingAllowed;
    private boolean petsAllowed;
}