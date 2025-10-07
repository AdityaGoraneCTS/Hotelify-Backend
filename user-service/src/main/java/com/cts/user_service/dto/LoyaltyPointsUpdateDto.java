package com.cts.user_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoyaltyPointsUpdateDto {

    @NotNull(message = "Loyalty points cannot be null")
    @Min(value = 0, message = "Loyalty points cannot be negative")
    private Long loyaltyPoints;
}
