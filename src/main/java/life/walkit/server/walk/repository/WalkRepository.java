package life.walkit.server.walk.repository;

import life.walkit.server.walk.entity.Walk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalkRepository extends JpaRepository<Walk, Long> {
}
