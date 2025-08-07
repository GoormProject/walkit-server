package life.walkit.server.review.repository;

import life.walkit.server.review.dto.ReviewStats;
import life.walkit.server.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByTrail_TrailIdOrderByCreatedAtDesc(Long trailId);

    @Query("""
    SELECT new life.walkit.server.review.dto.ReviewStats(
        r.trail.trailId,
        AVG(r.rating),
        COUNT(r)
    )
    FROM Review r
    WHERE r.trail.trailId = :trailId
    GROUP BY r.trail.trailId
""")
    Optional<ReviewStats> findReviewStatsByTrailId(Long trailId);
}
