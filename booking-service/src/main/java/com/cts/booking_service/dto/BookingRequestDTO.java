package com.cts.booking_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookingRequestDTO {

    @NotBlank(message = "Room ID is mandatory")
    private String roomId;

    @NotBlank(message = "Hotel ID is mandatory")
    private String hotelId;

    @NotNull(message = "Check-in date is mandatory")
    @FutureOrPresent(message = "Check-in date must be in the present or future")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is mandatory")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @NotNull(message = "Booking date is mandatory")
    @PastOrPresent(message = "Booking date must be in the past or present")
    private LocalDate bookingDate;

    @NotNull(message = "Number of rooms is mandatory")
    @Min(value = 1, message = "Must book at least one room")
    private Integer numberOfRooms;

    @NotNull(message = "Total amount is mandatory")
    @Positive(message = "Total amount must be positive")
    private BigDecimal totalAmount;

    @PositiveOrZero(message = "Discount cannot be negative")
    private BigDecimal discountApplied;

    @NotNull(message = "Final amount is mandatory")
    @Positive(message = "Final amount must be positive")
    private BigDecimal finalAmount;

    @NotBlank(message = "Payment method is mandatory")
    private String paymentMethod;

    @NotNull(message = "Guest details are mandatory")
    @Valid
    private GuestDetailsDTO guestDetails;
}