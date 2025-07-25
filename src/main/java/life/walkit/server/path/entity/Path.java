package life.walkit.server.path.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;

@Entity
@Table(name = "path")
@Getter
@NoArgsConstructor
public class Path {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "path_id")
    private Long pathId;

    @Column(name = "path", nullable = false, columnDefinition = "geometry(LineString, 4326)")
    private LineString path;

    public Path(LineString path) {
        this.path = path;
    }
}
