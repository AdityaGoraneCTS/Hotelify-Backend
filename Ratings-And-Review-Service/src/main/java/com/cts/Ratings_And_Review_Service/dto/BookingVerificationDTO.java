package com.cts.Ratings_And_Review_Service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

// This DTO defines the ONLY fields the Review Service cares about.
@Data
// This annotation explicitly tells the JSON parser to ignore any other fields
// from the Booking Service's response (like paymentMethod, guestDetails, etc.)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingVerificationDTO {
    private String userId;
    // The `bookingId` isn't even needed here, as we already have it.
}