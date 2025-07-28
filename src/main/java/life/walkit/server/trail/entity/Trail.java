package life.walkit.server.trail.entity;

import jakarta.persistence.*;
import life.walkit.server.global.BaseEntity;
import life.walkit.server.member.entity.Member;
import life.walkit.server.path.entity.Path;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "trail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trail_id")
    private Long trailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "member_id",
        nullable = false
    )
    private Member member;

    @OneToOne(
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @JoinColumn(
        name = "path_id",
        nullable = false
    )
    private Path path;

    @Column(
        name = "title",
        nullable = false
    )
    private String title;

    @Column(name = "description")
    private String description;

    @Column(
        name = "distance",
        nullable = false
    )
    private Double distance;

    @Column(
        name = "location",
        nullable = false
    )
    private String location;

    @Builder
    public Trail(
        Member member,
        Path path,
        String title,
        String description,
        Double distance,
        String location
    ) {
        this.member = member;
        this.path = path;
        this.title = title;
        this.description = description;
        this.distance = distance;
        this.location = location;
    }
}
