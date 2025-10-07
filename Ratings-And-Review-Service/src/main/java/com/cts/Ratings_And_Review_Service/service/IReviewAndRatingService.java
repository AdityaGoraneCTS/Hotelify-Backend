package com.cts.Ratings_And_Review_Service.service;

import com.cts.Ratings_And_Review_Service.dto.ReviewRequestDTO;
import com.cts.Ratings_And_Review_Service.dto.ReviewResponseDTO;

import java.util.List;

public interface IReviewAndRatingService {
    public ReviewResponseDTO createReview(ReviewRequestDTO reviewRequestDTO, String userId, String roles);

    public List<ReviewResponseDTO> getReviewsByHotelId(String hotelId);

    public Double getAverageRatingByHotelId(String hotelId);

    public Long getReviewCountByHotelId(String hotelId);
}
