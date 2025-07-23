package life.walkit.server.trail.entity;

import io.micrometer.core.annotation.Counted;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "trail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trail_id")
    private Long trailId;

    // TODO: Member 엔티티 추가시 주석 처리 해제
    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;*/

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "distance", nullable = false)
    private Double distance;

    // TODO: SRID 설정
    @Column(name = "location", nullable = false)
    private Point location;

    // TODO: SRID 설정
    @Column(name = "path", nullable = false)
    private LineString path;

    @Column(name = "create_at")
    @CreatedDate
    private LocalDateTime createdAt;
}
