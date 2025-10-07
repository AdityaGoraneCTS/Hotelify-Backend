package com.cts.booking_service.models;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class GuestDetails {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}
