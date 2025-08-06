package life.walkit.server.path.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "path")
@Getter
@NoArgsConstructor
public class Path {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "path_id")
    private Long pathId;

    @Column(
        name = "path",
        columnDefinition = "geometry(LineString, 4326)"
    )
    private LineString path;

    @Column(
        name = "start_point",
        columnDefinition = "geometry(Point, 4326)"
    )
    private Point point;


    @Builder
    public Path(
        LineString path,
        Point point
    ) {
        this.path = path;
        this.point = point;
    }
}
