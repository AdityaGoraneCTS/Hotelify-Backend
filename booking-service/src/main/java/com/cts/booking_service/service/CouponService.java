package com.cts.booking_service.service;

import com.cts.booking_service.dto.CouponValidateRequestDTO;
import com.cts.booking_service.dto.CouponValidateResponseDTO;

public interface CouponService {
    CouponValidateResponseDTO validateCoupon(CouponValidateRequestDTO requestDTO);
}