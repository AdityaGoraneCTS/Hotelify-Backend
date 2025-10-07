package com.cts.booking_service.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AvailabilityRequestDTO {

    @NotBlank(message = "hotelId is required")
    private String hotelId;

    @NotBlank(message = "roomId is required")
    private String roomId;

    @NotNull(message = "checkInDate is required")
    @Future(message = "Check-in date must be in the future")
    private LocalDate checkInDate;

    @NotNull(message = "checkOutDate is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @Min(value = 1, message = "requiredNumberOfRooms must be at least 1")
    private int requiredNumberOfRooms;

    @Min(value = 1, message = "quantityOfRooms must be at least 1")
    private int quantityOfRooms;
}
