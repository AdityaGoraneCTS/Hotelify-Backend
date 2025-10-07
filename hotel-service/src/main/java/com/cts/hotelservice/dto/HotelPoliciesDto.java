package com.cts.hotelservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelPoliciesDto {
    @NotBlank(message = "Check-in time is required")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Check-in time must be in HH:mm format")
    private String checkInTime;
    @NotBlank(message = "Check-out time is required")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Check-out time must be in HH:mm format")
    private String checkOutTime;
    private String cancellationPolicy;
    private boolean smokingAllowed;
    private boolean petsAllowed;
}
