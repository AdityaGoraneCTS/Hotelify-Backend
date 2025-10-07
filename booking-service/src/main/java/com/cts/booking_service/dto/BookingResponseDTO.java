package com.cts.booking_service.dto;

import com.cts.booking_service.models.BookingStatus;
import com.cts.booking_service.models.PaymentMethod;
import lombok.Data;

@Data
public class BookingResponseDTO {
    private String id;
    private String userId;
    private String roomId;
    private String hotelId;
    private String checkInDate;
    private String checkOutDate;
    private String bookingDate;
    private int numberOfRooms;
    private double totalAmount;
    private Double discountApplied;
    private double finalAmount;
    private PaymentMethod paymentMethod;
    private BookingStatus status;
    private GuestDetailsDTO guestDetails;
}