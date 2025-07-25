package life.walkit.server.path.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.locationtech.jts.geom.LineString;

@Entity
@Table(name = "path")
@Getter
public class Path {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pathId;

    @Column(name = "path", nullable = false, columnDefinition = "geometry(LineString, 4326)")
    private LineString path;
}
