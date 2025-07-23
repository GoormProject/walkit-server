package life.walkit.server.trail.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "trail_iamge")
public class TrailImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trail_image_id")
    private Long trailImageId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trail_id", nullable = false)
    private Trail trail;

    @Column(name = "route_image", nullable = false)
    private String routeImage;
}
