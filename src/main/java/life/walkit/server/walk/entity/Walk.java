package life.walkit.server.walk.entity;

import jakarta.persistence.*;
import life.walkit.server.member.entity.Member;
import life.walkit.server.path.entity.Path;
import life.walkit.server.trail.entity.Trail;
import lombok.AccessLevel;
import lombok.Builder;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trail_id")
    private Trail trail;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "date")
    private LocalDate date;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "path_id", nullable = false)
    private Path path;

    @Column(name = "total_time")
    private Duration totalTime;

    @Column(name = "pace")
    private Double pace;

    @Builder
    public Walk(Member member, Trail trail, LocalDateTime startedAt, LocalDateTime endedAt, LocalDate date, Path path, Duration totalTime, Double pace) {
        this.member = member;
        this.trail = trail;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.date = date;
        this.path = path;
        this.totalTime = totalTime;
        this.pace = pace;
    }
}
