package com.cts.Ratings_And_Review_Service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDTO {
    private Long id;
    private String userId;
    private String hotelId;
    private String bookingId;
    private Integer rating;
    private String comment;
    private LocalDateTime timestamp;
    private String reviewerName;
}
