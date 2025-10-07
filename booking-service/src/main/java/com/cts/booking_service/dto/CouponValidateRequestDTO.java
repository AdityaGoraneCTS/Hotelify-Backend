package com.cts.booking_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CouponValidateRequestDTO {

    @NotBlank(message = "couponCode is required")
    private String couponCode;

    @Min(value = 0, message = "totalAmount must be a positive value")
    private double totalAmount;
}