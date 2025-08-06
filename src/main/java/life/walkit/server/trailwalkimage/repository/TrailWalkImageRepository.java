package life.walkit.server.trailwalkimage.repository;

import life.walkit.server.trail.entity.Trail;
import life.walkit.server.trailwalkimage.entity.TrailWalkImage;
import life.walkit.server.walk.entity.Walk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrailWalkImageRepository extends JpaRepository<TrailWalkImage, Long> {

    List<TrailWalkImage> findByWalk(Walk walk);

    <T> Optional<T> findFirstByTrail(Trail trail);

    Optional<?> findFirstByWalk(Walk walk);
}
