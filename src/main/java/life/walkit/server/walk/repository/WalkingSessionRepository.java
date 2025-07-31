package life.walkit.server.walk.repository;

import life.walkit.server.walk.entity.Walk;
import life.walkit.server.walk.entity.WalkingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WalkingSessionRepository extends JpaRepository<WalkingSession, Long> {
    List<WalkingSession> findByWalk(Walk walk);

    Optional<WalkingSession> findFirstByWalkOrderByEventTimeDesc(Walk walk);
}
