package life.walkit.server.walk.repository;

import life.walkit.server.walk.entity.WalkingSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalkingSessionRepository extends JpaRepository<WalkingSession, Long> {
}
