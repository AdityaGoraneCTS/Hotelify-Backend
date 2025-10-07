package com.cts.hotelservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelFeaturesAmenitiesUpdateDto {

    @NotEmpty(message = "Features list cannot be empty")
    private List<String> features;

    @NotEmpty(message = "Amenities list cannot be empty")
    private List<String> amenities;
}
