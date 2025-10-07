package com.cts.Ratings_And_Review_Service.service;

import com.cts.Ratings_And_Review_Service.dto.BookingVerificationDTO;
import com.cts.Ratings_And_Review_Service.dto.ReviewRequestDTO;
import com.cts.Ratings_And_Review_Service.dto.ReviewResponseDTO;
import com.cts.Ratings_And_Review_Service.entity.Review;
import com.cts.Ratings_And_Review_Service.exception.DuplicateReviewException;
import com.cts.Ratings_And_Review_Service.exception.UnAuthorizedException;
import com.cts.Ratings_And_Review_Service.repository.ReviewAndRatingRepository;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewAndRatingServiceImpl implements IReviewAndRatingService {

    // Dependencies are correctly declared as final.
    private final ReviewAndRatingRepository reviewAndRatingRepository;
    private final ModelMapper modelMapper;
    private final WebClient webClient;

    // This is now the ONLY constructor, so Spring knows exactly what to do.
    // It will find the beans for each parameter and inject them.
    public ReviewAndRatingServiceImpl(ReviewAndRatingRepository reviewAndRatingRepository,
                                      ModelMapper modelMapper,
                                      WebClient.Builder webClientBuilder) { // <-- Correctly injects the BUILDER
        this.reviewAndRatingRepository = reviewAndRatingRepository;
        this.modelMapper = modelMapper;
        // Use the builder to create the specific WebClient instance for this service
        this.webClient = webClientBuilder.baseUrl("http://BOOKING-SERVICE").build();
    }

    @Override
    public ReviewResponseDTO createReview(ReviewRequestDTO reviewRequestDTO, String userId, String roles) {
        if (reviewAndRatingRepository.existsByBookingId(reviewRequestDTO.getBookingId())) {
            throw new DuplicateReviewException("A review already exists for booking ID: " + reviewRequestDTO.getBookingId());
        }

        try {
            // THE FIX IS HERE: Add the .header() calls to propagate security context
            BookingVerificationDTO bookingDetails = webClient.get()
                    .uri("/api/bookings/{bookingId}", reviewRequestDTO.getBookingId())
                    .header("X-User-Id", userId) // Pass the user's ID
                    .header("X-Roles", roles)     // Pass the user's roles
                    .retrieve()
                    .bodyToMono(BookingVerificationDTO.class)
                    .block();

            // The original security check remains the same
            if (bookingDetails == null || !bookingDetails.getUserId().equals(userId)) {
                throw new UnAuthorizedException("Forbidden: You are not authorized to review a booking you did not make.");
            }
        } catch (Exception e) {
            // Now this catch block will more accurately represent a real error
            // (like the service being down), not a simple auth failure.
            throw new UnAuthorizedException("Could not verify booking details. The booking may not exist or the service may be down." + e);
        }

        Review review = modelMapper.map(reviewRequestDTO, Review.class);
        review.setUserId(userId);

        Review savedReview = reviewAndRatingRepository.save(review);
        return modelMapper.map(savedReview, ReviewResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByHotelId(String hotelId) {
        List<Review> reviews = reviewAndRatingRepository.findByHotelId(hotelId);
        return reviews.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private ReviewResponseDTO convertToResponseDTO(Review review) {
        return modelMapper.map(review, ReviewResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRatingByHotelId(String hotelId) {
        Double avgRating = reviewAndRatingRepository.getAverageRatingByHotelId(hotelId);
        return avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getReviewCountByHotelId(String hotelId) {
        return reviewAndRatingRepository.getReviewCountByHotelId(hotelId);
    }
}