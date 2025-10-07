package com.cts.booking_service.service;

import com.cts.booking_service.dto.AvailabilityRequestDTO;
import com.cts.booking_service.dto.AvailabilityResponseDTO;
import com.cts.booking_service.dto.BookingRequestDTO;
import com.cts.booking_service.dto.BookingResponseDTO;

import java.util.List;

public interface BookingService {
    BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO, String userId);
    BookingResponseDTO getBookingById(String bookingId);
    List<BookingResponseDTO> getBookingsByUserId(String userId);
    List<BookingResponseDTO> getBookingsByHotelIds(List<String> hotelIds);
    List<BookingResponseDTO> getBookingsByManagerId(String managerId);

    /**
     * Checks if a specified number of rooms are available for a given date range.
     * @param requestDTO The availability request details.
     * @return An availability response indicating if rooms are available.
     */
    AvailabilityResponseDTO checkAvailability(AvailabilityRequestDTO requestDTO);
}
