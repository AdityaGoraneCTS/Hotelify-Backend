package com.cts.hotelservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelImagesUpdateDto {

    @NotEmpty(message = "Images list cannot be empty")
    private List<String> images;
    private String primaryImage;
}
