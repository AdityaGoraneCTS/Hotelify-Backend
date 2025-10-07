package com.cts.booking_service.service;

import com.cts.booking_service.client.HotelServiceClient;
import com.cts.booking_service.dto.AvailabilityRequestDTO;
import com.cts.booking_service.dto.AvailabilityResponseDTO;
import com.cts.booking_service.dto.BookingRequestDTO;
import com.cts.booking_service.dto.BookingResponseDTO;
import com.cts.booking_service.dto.hotel.HotelDto;
import com.cts.booking_service.exception.ResourceNotFoundException;
import com.cts.booking_service.models.Booking;
import com.cts.booking_service.models.BookingStatus;
import com.cts.booking_service.models.PaymentMethod;
import com.cts.booking_service.repository.BookingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;
    private final HotelServiceClient hotelServiceClient;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ModelMapper modelMapper, HotelServiceClient hotelServiceClient) {
        this.bookingRepository = bookingRepository;
        this.modelMapper = modelMapper;
        this.hotelServiceClient = hotelServiceClient;
    }

    @Override
    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO, String userId) {
        // 1. Map DTO to the entity
        Booking booking = modelMapper.map(bookingRequestDTO, Booking.class);

        // 2. *** CRITICAL STEP: Set the userId from the trusted token parameter ***
        booking.setUserId(userId);

        // 3. Handle payment method and booking status
        try {
            PaymentMethod paymentMethod = PaymentMethod.valueOf(bookingRequestDTO.getPaymentMethod().toUpperCase());
            booking.setPaymentMethod(paymentMethod);

            if (paymentMethod == PaymentMethod.PAY_AT_PROPERTY) {
                booking.setStatus(BookingStatus.PENDING);
            } else {
                booking.setStatus(BookingStatus.CONFIRMED);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid paymentMethod value: " + bookingRequestDTO.getPaymentMethod());
        }

        // 4. Save the booking
        Booking savedBooking = bookingRepository.save(booking);

        // 5. Return the response DTO
        return modelMapper.map(savedBooking, BookingResponseDTO.class);
    }

    @Override
    public BookingResponseDTO getBookingById(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
        return modelMapper.map(booking, BookingResponseDTO.class);
    }

    @Override
    public List<BookingResponseDTO> getBookingsByUserId(String userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        if (bookings.isEmpty()) {
            throw new ResourceNotFoundException("No bookings found for user with ID: " + userId);
        }
        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDTO> getBookingsByHotelIds(List<String> hotelIds) {
        if (hotelIds == null || hotelIds.isEmpty()) {
            return List.of(); // Return empty list if input is empty or null
        }
        List<Booking> bookings = bookingRepository.findByHotelIdIn(hotelIds);
        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDTO> getBookingsByManagerId(String managerId) {
        // Step 1: Call the external HOTEL-SERVICE using the Feign client
        List<HotelDto> hotelsManaged = hotelServiceClient.getHotelsByManagerId(managerId);

        if (hotelsManaged == null || hotelsManaged.isEmpty()) {
            return List.of(); // No hotels for this manager, so no bookings
        }

        // Step 2: Extract the hotel IDs from the response
        List<String> hotelIds = hotelsManaged.stream()
                .map(HotelDto::getId)
                .collect(Collectors.toList());

        // Step 3: Use the extracted hotel IDs to find the bookings
        List<Booking> bookings = bookingRepository.findByHotelIdIn(hotelIds);

        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public AvailabilityResponseDTO checkAvailability(AvailabilityRequestDTO requestDTO) {
        // Step 1: Find all existing bookings that overlap with the requested dates.
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                requestDTO.getRoomId(),
                requestDTO.getCheckInDate(),
                requestDTO.getCheckOutDate()
        );

        // Step 2: Calculate the total number of rooms already booked during that period.
        int totalRoomsBooked = overlappingBookings.stream()
                .mapToInt(Booking::getNumberOfRooms)
                .sum();

        // Step 3: Calculate the number of available rooms.
        int availableRooms = requestDTO.getQuantityOfRooms() - totalRoomsBooked;

        // Step 4: Check if the available rooms are sufficient.
        if (availableRooms >= requestDTO.getRequiredNumberOfRooms()) {
            // Step 5 (Success): Rooms are available.
            String message = String.format("Success: %d rooms are available.", availableRooms);
            return new AvailabilityResponseDTO(true, message, availableRooms);
        } else {
            // Step 5 (Failure): Not enough rooms are available.
            String message;
            if (availableRooms <= 0) {
                message = "Sorry, no rooms are available for this slot. Please try other dates.";
            } else {
                message = String.format("Sorry, only %d room(s) are available for this slot. Please adjust your request.", availableRooms);
            }
            return new AvailabilityResponseDTO(false, message, availableRooms);
        }
    }
}