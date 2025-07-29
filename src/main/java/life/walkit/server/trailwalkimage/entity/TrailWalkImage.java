package life.walkit.server.trailwalkimage.entity;

import jakarta.persistence.*;
import life.walkit.server.trail.entity.Trail;
import life.walkit.server.walk.entity.Walk;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "trail_image")
@NoArgsConstructor
public class TrailWalkImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trail_walk_image_id")
    private Long trailImageId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "walk_id",
        nullable = false
    )
    private Walk walk;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "trail_id",
        nullable = true
    )
    private Trail trail;

    @Column(
        name = "route_image",
        nullable = false
    )
    private String routeImage;

    @Builder
    public TrailWalkImage(
        Trail trail,
        Walk walk,
        String routeImage
    ) {
        this.trail = trail;
        this.walk = walk;
        this.routeImage = routeImage;
    }
}
