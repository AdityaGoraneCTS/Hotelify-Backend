package com.cts.hotelservice.controller;

import com.cts.hotelservice.dto.*;
import com.cts.hotelservice.exception.UnAuthorizedException;
import com.cts.hotelservice.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    // =====================================================================================
    //                              PUBLIC ENDPOINTS
    // =====================================================================================

    @GetMapping("/{id}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable String id) {
        HotelDto hotelDto = hotelService.getHotelById(id);
        return ResponseEntity.ok(hotelDto);
    }

    // =====================================================================================
    //                              MANAGER / ADMIN ENDPOINTS
    // =====================================================================================

    @PostMapping
    public ResponseEntity<HotelDto> createHotel(@Valid @RequestBody HotelDto hotelDto,
                                                @RequestHeader("X-Roles") String roles,
                                                @RequestHeader("X-User-Id") String userId) {
        checkAdmin(roles);
        hotelDto.setManagerId(userId); // Set the owner from the token
        HotelDto savedHotel = hotelService.createHotel(hotelDto);
        return new ResponseEntity<>(savedHotel, HttpStatus.CREATED);
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<HotelDto>> getHotelsByManagerId(@PathVariable String managerId,
                                                               @RequestHeader("X-User-Id") String tokenUserId) {
        // A manager can only view their own hotels.
        if (!managerId.equals(tokenUserId)) {
            throw new UnAuthorizedException("You are not authorized to view this resource.");
        }
        List<HotelDto> hotelDtos = hotelService.getHotelsByManagerId(managerId);
        return ResponseEntity.ok(hotelDtos);
    }

    // Search Hotels By name and city
    @GetMapping("/search")
    public ResponseEntity<List<HotelDto>> searchHotels(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String type) {
        List<HotelDto> hotels = hotelService.searchHotels(location, type);
        return ResponseEntity.ok(hotels);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable String id,
                                            @RequestHeader("X-Roles") String roles,
                                            @RequestHeader("X-User-Id") String userId) {
        checkAdminAndOwner(roles, userId, id);
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/basic-info")
    public ResponseEntity<HotelDto> updateBasicInfo(@PathVariable String id,
                                                    @Valid @RequestBody HotelBasicInfoUpdateDto updateDto,
                                                    @RequestHeader("X-Roles") String roles,
                                                    @RequestHeader("X-User-Id") String userId) {
        checkAdminAndOwner(roles, userId, id);
        HotelDto updatedHotel = hotelService.updateBasicInfo(id, updateDto);
        return ResponseEntity.ok(updatedHotel);
    }

    @PatchMapping("/{id}/contact-and-address")
    public ResponseEntity<HotelDto> updateContactAndAddress(@PathVariable String id,
                                                            @Valid @RequestBody HotelContactAddressUpdateDto updateDto,
                                                            @RequestHeader("X-Roles") String roles,
                                                            @RequestHeader("X-User-Id") String userId) {
        checkAdminAndOwner(roles, userId, id);
        HotelDto updatedHotel = hotelService.updateContactAndAddress(id, updateDto);
        return ResponseEntity.ok(updatedHotel);
    }

    @PatchMapping("/{id}/images")
    public ResponseEntity<HotelDto> updateImages(@PathVariable String id,
                                                 @Valid @RequestBody HotelImagesUpdateDto updateDto,
                                                 @RequestHeader("X-Roles") String roles,
                                                 @RequestHeader("X-User-Id") String userId) {
        checkAdminAndOwner(roles, userId, id);
        HotelDto updatedHotel = hotelService.updateImagesAndMedia(id, updateDto);
        return ResponseEntity.ok(updatedHotel);
    }

    @PatchMapping("/{id}/features-and-amenities")
    public ResponseEntity<HotelDto> updateFeaturesAndAmenities(@PathVariable String id,
                                                               @Valid @RequestBody HotelFeaturesAmenitiesUpdateDto updateDto,
                                                               @RequestHeader("X-Roles") String roles,
                                                               @RequestHeader("X-User-Id") String userId) {
        checkAdminAndOwner(roles, userId, id);
        HotelDto updatedHotel = hotelService.updateFeaturesAndAmenities(id, updateDto);
        return ResponseEntity.ok(updatedHotel);
    }

    @PatchMapping("/{id}/policies")
    public ResponseEntity<HotelDto> updateHotelPolicies(@PathVariable String id,
                                                        @Valid @RequestBody HotelPoliciesUpdateDto updateDto,
                                                        @RequestHeader("X-Roles") String roles,
                                                        @RequestHeader("X-User-Id") String userId) {
        checkAdminAndOwner(roles, userId, id);
        HotelDto updatedHotel = hotelService.updateHotelPolicies(id, updateDto);
        return ResponseEntity.ok(updatedHotel);
    }

    @PatchMapping("/{id}/rooms")
    public ResponseEntity<HotelDto> updateRooms(@PathVariable String id,
                                                @Valid @RequestBody List<RoomUpdateDto> roomUpdates,
                                                @RequestHeader("X-Roles") String roles,
                                                @RequestHeader("X-User-Id") String userId) {
        checkAdminAndOwner(roles, userId, id);
        HotelDto updatedHotel = hotelService.updateRooms(id, roomUpdates);
        return ResponseEntity.ok(updatedHotel);
    }

    // =====================================================================================
    //                             PRIVATE AUTHORIZATION HELPERS
    // =====================================================================================

    /**
     * Checks if the user has the ROLE_ADMIN. Throws UnAuthorizedException if not.
     */
    private void checkAdmin(String roles) {
        if (roles == null || !roles.contains("ROLE_ADMIN")) {
            throw new UnAuthorizedException("You are not authorized to perform this action.");
        }
    }

    /**
     * Checks if the user is an admin AND the owner of the hotel.
     * This is the primary security method for all modification endpoints.
     */
    private void checkAdminAndOwner(String roles, String userId, String hotelId) {
        // 1. Must be an admin
        checkAdmin(roles);

        // 2. Must be the owner of the hotel.
        // The service layer will throw ResourceNotFoundException if the hotel doesn't exist.
        HotelDto hotel = hotelService.getHotelById(hotelId);

        if (!userId.equals(hotel.getManagerId())) {
            throw new UnAuthorizedException("Forbidden: You are not the owner of this hotel.");
        }
    }
}