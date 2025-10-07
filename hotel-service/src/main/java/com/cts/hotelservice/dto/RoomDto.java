package com.cts.hotelservice.dto;

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
public class RoomDto {
    private String id;
    @NotBlank(message = "Room name is required")
    private String name;
    private String description;
    @NotNull(message = "Original price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private Double originalPrice;
    private Double discountedPrice;
    private Double discountPercentage;
    @NotNull(message = "Total price including taxes is required")
    private Double totalPriceIncludesTaxes;
    @NotNull(message = "Maximum guests is required")
    @DecimalMin(value = "1", message = "Max guests must be at least 1")
    private Integer maxGuests;
    @NotBlank(message = "Bed type is required")
    private String bedType;
    private List<String> amenities;
    private List<String> images;
    private boolean isAvailable;
    private int quantityAvailable;
}
