package life.walkit.server.walk.entity;

import jakarta.persistence.*;
import life.walkit.server.member.entity.Member;
import life.walkit.server.path.entity.Path;
import life.walkit.server.trail.entity.Trail;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Builder.Default;

import java.time.Duration;

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
    @JoinColumn(
        name = "member_id",
        nullable = false
    )
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trail_id")
    private Trail trail;

    @OneToOne(
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @JoinColumn(
        name = "path_id"
    )
    private Path path;

    @Column(name = "walk_title")
    private String walkTitle;

    @Column(
        name = "total_distance"
    )
    private Double totalDistance;

    @Column(
        name = "total_time"
    )
    private Duration totalTime;

    @Column(
        name = "pace"
    )
    private Double pace;

    @Column(
        name = "is_uploaded",
        columnDefinition = "BOOLEAN DEFAULT FALSE",
        nullable = false
    )
    private Boolean isUploaded;

    public void updateWalkDetails(
        String walkTitle,
        Path path,
        Double totalDistance,
        Duration totalTime,
        Double pace
    ) {
        this.walkTitle = walkTitle;
        this.path = path;
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
        this.pace = pace;
    }

    public void updateTrail(Trail trail) {
        this.trail = trail;
    }

    public void updateIsUploaded(boolean isUploaded) {
        this.isUploaded = isUploaded;
    }

    @Builder
    public Walk(
        Member member,
        Trail trail,
        Path path,
        String walkTitle,
        Double totalDistance,
        Duration totalTime,
        Double pace
    ) {
        this.member = member;
        this.trail = trail;
        this.path = path;
        this.walkTitle = walkTitle;
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
        this.pace = pace;
        this.isUploaded = false;
    }

}
