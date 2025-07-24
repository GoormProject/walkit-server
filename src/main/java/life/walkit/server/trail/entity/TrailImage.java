package life.walkit.server.trail.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "trail_iamge")
@NoArgsConstructor
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

    public TrailImage(Trail trail, String routeImage) {
        this.trail = trail;
        this.routeImage = routeImage;
    }
}
