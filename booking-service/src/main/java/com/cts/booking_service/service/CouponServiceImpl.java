package com.cts.booking_service.service;

import com.cts.booking_service.dto.CouponValidateRequestDTO;
import com.cts.booking_service.dto.CouponValidateResponseDTO;
import com.cts.booking_service.exception.ResourceNotFoundException;
import com.cts.booking_service.models.Coupon;
import com.cts.booking_service.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Autowired
    public CouponServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    public CouponValidateResponseDTO validateCoupon(CouponValidateRequestDTO requestDTO) {
        // Find the coupon by code or throw a ResourceNotFoundException
        Coupon coupon = couponRepository.findByCode(requestDTO.getCouponCode())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon with code '" + requestDTO.getCouponCode() + "' not found."));

        // Validate the coupon based on multiple criteria
        if (!coupon.isValid()) {
            return buildInvalidResponse(requestDTO.getCouponCode(), "This coupon is no longer active.");
        }

        if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
            return buildInvalidResponse(requestDTO.getCouponCode(), "This coupon has expired.");
        }

        if (requestDTO.getTotalAmount() < coupon.getMinAmount()) {
            String message = String.format("A minimum purchase amount of %.2f is required to use this coupon.", coupon.getMinAmount());
            return buildInvalidResponse(requestDTO.getCouponCode(), message);
        }

        // If all checks pass, the coupon is valid
        double discountAmount = coupon.getDiscount();
        double finalAmount = requestDTO.getTotalAmount() - discountAmount;

        return CouponValidateResponseDTO.builder()
                .isValid(true)
                .message("Coupon applied successfully!")
                .couponCode(coupon.getCode())
                .discountAmount(discountAmount)
                .finalAmount(finalAmount < 0 ? 0 : finalAmount) // Ensure final amount is not negative
                .build();
    }

    private CouponValidateResponseDTO buildInvalidResponse(String code, String message) {
        return CouponValidateResponseDTO.builder()
                .isValid(false)
                .message(message)
                .couponCode(code)
                .build();
    }
}