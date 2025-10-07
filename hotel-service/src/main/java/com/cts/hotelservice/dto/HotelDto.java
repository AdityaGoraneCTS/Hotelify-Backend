package com.cts.hotelservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelDto {

    private String id;
    @NotBlank(message = "Hotel name is required")
    private String name;
    @NotBlank(message = "Hotel description is required")
    private String description;
    @Valid
    @NotNull(message = "Address is required")
    private AddressDto address;
    private String type;
    private List<String> features;
    private List<String> amenities;
    @DecimalMin(value = "0.0", message = "Rating must be a positive number")
    private double rating;
    private int reviews;
    private List<String> images;
    private String primaryImage;
    private boolean isFullyRefundable;
    private boolean hasFreeBreakfast;
    private boolean reserveNowPayLater;
    @Valid
    @NotNull(message = "Hotel policies are required")
    private HotelPoliciesDto policies;
    @Valid
    private List<RoomDto> rooms;
    @Valid
    @NotNull(message = "Contact information is required")
    private ContactDto contact;
    private String availabilityMessage;
    private String managerId;
}
