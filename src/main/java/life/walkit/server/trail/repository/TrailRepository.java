package life.walkit.server.trail.repository;

import life.walkit.server.trail.entity.Trail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrailRepository extends JpaRepository<Trail, Long> {
}
