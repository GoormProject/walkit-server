package life.walkit.server.review.dto;

import life.walkit.server.review.entity.Review;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record ReviewListResponse(

        Long trailId,
        Double rating,
        ReviewResponse myReview,
        List<ReviewResponse> reviews
) {

    public static ReviewListResponse of(Long trailId, Review myReview, List<Review> reviews) {
        return ReviewListResponse.builder()
                .trailId(trailId)
                .rating(reviews.stream().mapToDouble(Review::getRating).average().orElse(0))
                .myReview(ReviewResponse.of(myReview))
                .reviews(reviews.stream().map(ReviewResponse::of).toList())
                .build();
    }
}
