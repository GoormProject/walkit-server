package life.walkit.server.review.entity;

import jakarta.persistence.*;
import life.walkit.server.trail.entity.Trail;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    // TODO: Member 엔티티 추가시 주석 처리 해제
    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trail_id", nullable = false)
    private Trail trail;

    // TODO: BasyEntity로 상속
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "content")
    private String content;

    @Column(name = "rating", nullable = false)
    @Check(constraints = "rating >= 1 AND rating <= 5")
    private Integer rating;
}
