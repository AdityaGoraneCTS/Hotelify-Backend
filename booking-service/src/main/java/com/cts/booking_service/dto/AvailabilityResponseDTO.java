package com.cts.booking_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResponseDTO {
    private boolean available;
    private String message;
    private int availableRooms;
}
