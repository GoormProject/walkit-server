package life.walkit.server.path.repository;

import life.walkit.server.path.entity.Path;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PathRepository extends JpaRepository<Path, Long> {
}
