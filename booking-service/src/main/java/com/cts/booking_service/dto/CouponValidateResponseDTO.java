package com.cts.booking_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponValidateResponseDTO {
    private boolean isValid;
    private String message;
    private String couponCode;
    private Double discountAmount;
    private Double finalAmount;
}
