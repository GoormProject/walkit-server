package life.walkit.server.walk.entity;

import jakarta.persistence.*;
import life.walkit.server.trail.entity.Trail;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "walk")
public class Walk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "walk_id")
    private Long walkId;

    // TODO: Member 엔티티 추가시 주석 처리 해제
    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trail_id", nullable = false)
    private Trail trail;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "path", columnDefinition = "geometry(Point, 4326)")
    private LineString path;

    @Column(name = "total_time")
    private Duration totalTime;

    @Column(name = "pace")
    private Double pace;
}
