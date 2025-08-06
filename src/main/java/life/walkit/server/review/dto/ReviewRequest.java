package life.walkit.server.review.dto;

import jakarta.validation.constraints.*;
import life.walkit.server.member.entity.Member;
import life.walkit.server.review.entity.Review;
import life.walkit.server.trail.entity.Trail;

public record ReviewRequest(

        @NotBlank(message = "리뷰 내용을 입력해주세요.")
        @Size(max = 2000, message = "리뷰 내용은 2000자를 초과할 수 없습니다.")
        String content,

        @NotNull(message = "평점을 입력해주세요.")
        @Min(value = 1, message = "최소 평점은 1입니다.")
        @Max(value = 5, message = "최대 평점은 5입니다.")
        Integer rating,

        @NotNull(message = "산책로 ID는 필수입니다.")
        Long trailId
) {

        public Review toEntity(Member member, Trail trail) {
                return Review.builder()
                        .member(member)
                        .trail(trail)
                        .content(content)
                        .rating(rating)
                        .build();
        }
}
