package com.cts.booking_service.repository;

import com.cts.booking_service.models.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    /**
     * Finds a coupon by its unique code.
     * @param code The coupon code to search for.
     * @return An Optional containing the coupon if found.
     */
    Optional<Coupon> findByCode(String code);
}
