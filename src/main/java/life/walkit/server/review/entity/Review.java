package life.walkit.server.review.entity;

import jakarta.persistence.*;
import life.walkit.server.global.BaseEntity;
import life.walkit.server.member.entity.Member;
import life.walkit.server.review.dto.request.ReviewRequest;
import life.walkit.server.review.dto.request.ReviewUpdateRequest;
import life.walkit.server.trail.entity.Trail;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trail_id", nullable = false)
    private Trail trail;

    @Column(name = "content")
    private String content;

    @Column(name = "rating", nullable = false)
    @Check(constraints = "rating >= 1 AND rating <= 5")
    private Integer rating;

    @Builder
    public Review(Member member, Trail trail, String content, Integer rating) {
        this.member = member;
        this.trail = trail;
        this.content = content;
        this.rating = rating;
    }

    public void updateReview(ReviewUpdateRequest request) {
        this.content = request.content();
        this.rating = request.rating();
    }
}
