package life.walkit.server.review.service;

import life.walkit.server.member.entity.Member;
import life.walkit.server.member.error.MemberException;
import life.walkit.server.member.error.enums.MemberErrorCode;
import life.walkit.server.member.repository.MemberRepository;
import life.walkit.server.review.dto.request.ReviewRequest;
import life.walkit.server.review.dto.ReviewResponse;
import life.walkit.server.review.dto.request.ReviewUpdateRequest;
import life.walkit.server.review.entity.Review;
import life.walkit.server.review.error.ReviewException;
import life.walkit.server.review.error.enums.ReviewErrorCode;
import life.walkit.server.review.repository.ReviewRepository;
import life.walkit.server.trail.entity.Trail;
import life.walkit.server.trail.error.enums.TrailErrorCode;
import life.walkit.server.trail.error.enums.TrailException;
import life.walkit.server.trail.repository.TrailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final MemberRepository memberRepository;
    private final TrailRepository trailRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public ReviewResponse createReview(Long memberId, ReviewRequest request) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND)
        );
        Trail trail = trailRepository.findById(request.trailId()).orElseThrow(
                () -> new TrailException(TrailErrorCode.TRAIL_NOT_FOUND)
        );

        Review review = reviewRepository.save(request.toEntity(member, trail));
        return ReviewResponse.of(review);
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, Long memberId, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND)
        );

        if (!Objects.equals(review.getMember().getMemberId(), memberId)) {
            throw new ReviewException(ReviewErrorCode.REVIEW_FORBIDDEN);
        }

        review.updateReview(request);
        return ReviewResponse.of(reviewRepository.save(review));
    }
}
