package com.cts.Ratings_And_Review_Service.controller;

import com.cts.Ratings_And_Review_Service.dto.ReviewRequestDTO;
import com.cts.Ratings_And_Review_Service.dto.ReviewResponseDTO;
import com.cts.Ratings_And_Review_Service.exception.UnAuthorizedException;
import com.cts.Ratings_And_Review_Service.service.IReviewAndRatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewAndRatingController {

    @Autowired
    IReviewAndRatingService reviewAndRatingService;

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(@Valid @RequestBody ReviewRequestDTO reviewRequestDTO,
                                                          @RequestHeader("X-Roles") String roles,
                                                          @RequestHeader("X-User-Id") String userId) {
        checkUser(roles);
        // Pass ALL relevant headers to the service layer
        ReviewResponseDTO createdReview = reviewAndRatingService.createReview(reviewRequestDTO, userId, roles);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByHotelId(@PathVariable String hotelId) {
        List<ReviewResponseDTO> reviews = reviewAndRatingService.getReviewsByHotelId(hotelId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/hotel/{hotelId}/average-rating")
    public ResponseEntity<Map<String, Object>> getAverageRating(@PathVariable String hotelId) {
        Double averageRating = reviewAndRatingService.getAverageRatingByHotelId(hotelId);
        Long reviewCount = reviewAndRatingService.getReviewCountByHotelId(hotelId);

        return ResponseEntity.ok(Map.of(
                "hotelId", hotelId,
                "averageRating", averageRating,
                "reviewCount", reviewCount
        ));
    }

    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        return ResponseEntity.ok("Review service is running!");
    }

    private void checkUser(String roles) {
        if (roles == null || !roles.contains("ROLE_USER")) {
            // Log the unauthorized access attempt
            System.out.println("Unauthorized access attempt. Roles: " + roles);
            throw new UnAuthorizedException("You are not authorized to perform this action.");
        }
    }

}
