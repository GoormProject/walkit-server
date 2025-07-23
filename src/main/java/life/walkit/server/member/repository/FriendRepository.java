package life.walkit.server.member.repository;

import life.walkit.server.member.entity.Friend;
import life.walkit.server.member.entity.Member;
import life.walkit.server.member.entity.enums.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    List<Friend> findByMemberAndStatus(Member member, FriendStatus status);

    List<Friend> findByPartnerAndStatus(Member member, FriendStatus status);

}
