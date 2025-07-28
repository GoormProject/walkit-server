package life.walkit.server.friend.repository;

import life.walkit.server.friend.entity.Friend;
import life.walkit.server.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    // 특정 회원(member)에 대한 친구 목록 조회
    List<Friend> findAllByMember(Member member);

    boolean existsByMemberAndPartner(Member member, Member member1);
}
