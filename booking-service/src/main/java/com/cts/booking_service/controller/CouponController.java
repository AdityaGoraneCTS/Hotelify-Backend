package com.cts.booking_service.controller;

import com.cts.booking_service.dto.CouponValidateRequestDTO;
import com.cts.booking_service.dto.CouponValidateResponseDTO;
import com.cts.booking_service.exception.UnAuthorizedException;
import com.cts.booking_service.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    @Autowired
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/validate")
    public ResponseEntity<CouponValidateResponseDTO> validateCoupon(@Valid @RequestBody CouponValidateRequestDTO requestDTO,
                                                                    @RequestHeader HttpHeaders headers) {
        String roles=headers.get("x-roles").toString();
        System.out.println(roles);
        if(roles.contains("ROLE_USER")) {
            CouponValidateResponseDTO response = couponService.validateCoupon(requestDTO);
            return ResponseEntity.ok(response);
        }
        throw new UnAuthorizedException("Your not authorized");
    }
}