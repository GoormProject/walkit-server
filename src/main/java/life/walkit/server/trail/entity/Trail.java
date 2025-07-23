package life.walkit.server.trail.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

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

    @Column(name = "location", nullable = false, columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @Column(name = "path", nullable = false, columnDefinition = "geometry(LineString, 4326)")
    private LineString path;

    // TODO: BasyEntity로 상속
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
