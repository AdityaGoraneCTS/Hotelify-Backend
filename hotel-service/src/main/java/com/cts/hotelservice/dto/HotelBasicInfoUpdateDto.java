package com.cts.hotelservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelBasicInfoUpdateDto {

    @NotBlank(message = "Hotel name is required")
    private String name;

    @NotBlank(message = "Hotel description is required")
    private String description;

    @NotBlank(message = "Hotel type is required")
    private String type;
}
