package life.walkit.server.trail.repository;

import life.walkit.server.member.entity.Member;
import life.walkit.server.trail.entity.Trail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrailRepository extends JpaRepository<Trail, Long> {
    List<Trail> findByMember(Member member);

    List<Trail> findByTitle(String title);
}
