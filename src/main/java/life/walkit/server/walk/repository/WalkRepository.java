package life.walkit.server.walk.repository;

import life.walkit.server.member.entity.Member;
import life.walkit.server.walk.entity.Walk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalkRepository extends JpaRepository<Walk, Long> {
    List<Walk> findByMember(Member member);
}
