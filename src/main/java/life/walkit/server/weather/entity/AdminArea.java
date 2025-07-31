package life.walkit.server.weather.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@Table(name = "admin_area")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_area_id")
    private Long adminAreaId;

    @Column(name = "sido", nullable = false)
    private String sido;

    @Column(name = "sigungu", nullable = false)
    private String sigungu;

    @Column(name = "eupmyeondong", nullable = false)
    private String eupmyeondong;

    @Column(name = "location", columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @Column(name = "x", nullable = false)
    private Integer X;

    @Column(name = "y", nullable = false)
    private Integer Y;
}
