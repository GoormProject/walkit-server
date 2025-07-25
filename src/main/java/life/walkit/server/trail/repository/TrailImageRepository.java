package life.walkit.server.trail.repository;

import life.walkit.server.trail.entity.Trail;
import life.walkit.server.trail.entity.TrailImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrailImageRepository extends JpaRepository<TrailImage, Long> {

    List<TrailImage> findByTrail(Trail trail);
}
