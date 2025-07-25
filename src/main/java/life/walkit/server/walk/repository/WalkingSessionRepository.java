package life.walkit.server.walk.repository;

import life.walkit.server.walk.entity.Walk;
import life.walkit.server.walk.entity.WalkingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalkingSessionRepository extends JpaRepository<WalkingSession, Long> {
    List<WalkingSession> findByWalk(Walk walk);
}
