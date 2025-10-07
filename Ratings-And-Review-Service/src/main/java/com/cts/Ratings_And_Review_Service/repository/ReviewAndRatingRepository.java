package com.cts.Ratings_And_Review_Service.repository;

import com.cts.Ratings_And_Review_Service.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewAndRatingRepository extends JpaRepository<Review, Long> {
    List<Review> findByHotelId(String hotelId);

    boolean existsByBookingId(String bookingId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.hotelId = :hotelId")
    Double getAverageRatingByHotelId(@Param("hotelId") String hotelId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.hotelId = :hotelId")
    Long getReviewCountByHotelId(@Param("hotelId") String hotelId);
}
