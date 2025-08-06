package life.walkit.server.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import life.walkit.server.auth.dto.CustomMemberDetails;
import life.walkit.server.global.response.BaseResponse;
import life.walkit.server.review.dto.request.ReviewRequest;
import life.walkit.server.review.dto.ReviewResponse;
import life.walkit.server.review.dto.enums.TrailReviewResponse;
import life.walkit.server.review.dto.request.ReviewUpdateRequest;
import life.walkit.server.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "산책로",
        description = "산책로 관련 API"
)
@SecurityRequirement(name = "cookieAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trails")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "산책로 리뷰 등록", description = "새로운 리뷰를 작성합니다.")
    @PostMapping("/reviews/new")
    public ResponseEntity<BaseResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal CustomMemberDetails member,
            @Valid @RequestBody ReviewRequest reviewRequest
    ) {

        return BaseResponse.toResponseEntity(
                TrailReviewResponse.CREATE_REVIEW_SUCCESS,
                reviewService.createReview(member.getMemberId(), reviewRequest)
        );
    }

    @Operation(summary = "산책로 리뷰 수정", description = "작성한 리뷰를 수정합니다.")
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<BaseResponse<ReviewResponse>> updateReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomMemberDetails member,
            @Valid @RequestBody ReviewUpdateRequest reviewRequest
    ) {

        return BaseResponse.toResponseEntity(
                TrailReviewResponse.UPDATE_REVIEW_SUCCESS,
                reviewService.updateReview(reviewId, member.getMemberId(), reviewRequest)
        );
    }

    @Operation(summary = "산책로 리뷰 삭제", description = "작성한 리뷰를 삭제합니다.")
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<BaseResponse<Void>> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomMemberDetails member
    ) {

        reviewService.deleteReview(reviewId, member.getMemberId());
        return BaseResponse.toResponseEntity(TrailReviewResponse.DELETE_REVIEW_SUCCESS);
    }
}
