package com.cts.booking_service.dto.hotel;

import lombok.Data;
import java.util.List;

// This DTO mirrors the response from the Hotel Service
@Data
public class HotelDto {
    private String id;
    private String name;
    private String managerId;
    // We only need the id and managerId for this logic,
    // but other fields can be added if needed later.
    // private String description;
    // private AddressDto address;
    // ... other fields
}
