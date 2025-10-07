package com.cts.booking_service.controller;

import com.cts.booking_service.dto.AvailabilityRequestDTO;
import com.cts.booking_service.dto.AvailabilityResponseDTO;
import com.cts.booking_service.dto.BookingRequestDTO;
import com.cts.booking_service.dto.BookingResponseDTO;
import com.cts.booking_service.exception.UnAuthorizedException;
import com.cts.booking_service.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // =====================================================================================
    //                              PUBLIC ENDPOINT
    // =====================================================================================

    /**
     * Checks room availability. This is a public endpoint and requires no authentication.
     */
    @PostMapping("/availability")
    public ResponseEntity<AvailabilityResponseDTO> checkRoomAvailability(@Valid @RequestBody AvailabilityRequestDTO availabilityRequestDTO) {
        if (availabilityRequestDTO.getCheckInDate().isAfter(availabilityRequestDTO.getCheckOutDate())) {
            AvailabilityResponseDTO response = new AvailabilityResponseDTO(false, "Check-in date must be before check-out date.", 0);
            return ResponseEntity.badRequest().body(response);
        }
        AvailabilityResponseDTO availability = bookingService.checkAvailability(availabilityRequestDTO);
        return ResponseEntity.ok(availability);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDTO> getSingleBooking(@PathVariable String bookingId,
                                                               @RequestHeader("X-User-Id") String tokenUserId,
                                                               @RequestHeader("X-Roles") String roles) {
//        checkAdmin(roles);
        BookingResponseDTO booking = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(booking);
    }

    // =====================================================================================
    //                              USER (ROLE_USER) ENDPOINTS
    // =====================================================================================

    /**
     * Creates a new booking for the authenticated user.
     * The userId is extracted from the token, not the request body.
     */
    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingRequestDTO bookingRequestDTO,
                                                            @RequestHeader("X-Roles") String roles,
                                                            @RequestHeader("X-User-Id") String userId) {
        checkUser(roles);

        // Call the updated service method, passing the trusted userId from the token
        BookingResponseDTO createdBooking = bookingService.createBooking(bookingRequestDTO, userId);

        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }

    /**
     * Retrieves all bookings for the currently authenticated user.
     * Prevents one user from seeing another user's bookings.
     */
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponseDTO>> getMyBookings(@RequestHeader("X-Roles") String roles,
                                                                  @RequestHeader("X-User-Id") String userId) {
        checkUser(roles);
        List<BookingResponseDTO> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    // =====================================================================================
    //                              ADMIN (ROLE_ADMIN) ENDPOINTS
    // =====================================================================================

    /**
     * Retrieves bookings by a list of hotel IDs. Restricted to admins.
     */
    @GetMapping("/search/by-hotels")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByHotelIds(@RequestParam List<String> hotelIds,
                                                                          @RequestHeader("X-Roles") String roles) {
        checkAdmin(roles);
        List<BookingResponseDTO> bookings = bookingService.getBookingsByHotelIds(hotelIds);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Retrieves all bookings associated with a specific hotel manager. Restricted to admins.
     */
    @GetMapping("/search/by-manager/{managerId}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByManagerId(@PathVariable String managerId,
                                                                           @RequestHeader("X-Roles") String roles) {
        checkAdmin(roles);
        List<BookingResponseDTO> bookings = bookingService.getBookingsByManagerId(managerId);
        return ResponseEntity.ok(bookings);
    }

    // =====================================================================================
    //                             PRIVATE AUTHORIZATION HELPERS
    // =====================================================================================

    private void checkUser(String roles) {
        if (roles == null || !roles.contains("ROLE_USER")) {
            throw new UnAuthorizedException("You are not authorized to perform this action.");
        }
    }

    private void checkAdmin(String roles) {
        if (roles == null || !roles.contains("ROLE_ADMIN")) {
            throw new UnAuthorizedException("You are not authorized to perform this action.");
        }
    }
}