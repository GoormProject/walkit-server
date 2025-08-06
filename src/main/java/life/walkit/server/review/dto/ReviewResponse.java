package life.walkit.server.review.dto;

import jakarta.validation.constraints.*;
import life.walkit.server.review.entity.Review;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReviewResponse(

        Long reviewId,
        String content,
        Integer rating,
        Long trailId,
        LocalDateTime createdAt
) {

    public static ReviewResponse of(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .content(review.getContent())
                .rating(review.getRating())
                .trailId(review.getTrail().getTrailId())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
