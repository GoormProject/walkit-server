package life.walkit.server.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import life.walkit.server.auth.dto.CustomMemberDetails;
import life.walkit.server.global.response.BaseResponse;
import life.walkit.server.review.dto.ReviewRequest;
import life.walkit.server.review.dto.ReviewResponse;
import life.walkit.server.review.dto.enums.TrailReviewResponse;
import life.walkit.server.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "산책로 리뷰",
        description = "산책로 리뷰 API"
)
@SecurityRequirement(name = "cookieAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trails")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "산책로 리뷰 등록", description = "새로운 리뷰를 작성합니다.")
    @PostMapping("/new")
    public ResponseEntity<BaseResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal CustomMemberDetails member,
            @Valid @RequestBody ReviewRequest reviewRequest
    ) {

        return BaseResponse.toResponseEntity(
                TrailReviewResponse.CREATE_REVIEW_SUCCESS,
                reviewService.createReview(member.getMemberId(), reviewRequest)
        );
    }



}
