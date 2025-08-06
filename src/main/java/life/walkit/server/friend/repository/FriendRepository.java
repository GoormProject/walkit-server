package life.walkit.server.friend.repository;

import life.walkit.server.friend.entity.Friend;
import life.walkit.server.member.entity.Member;
import life.walkit.server.member.entity.enums.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findAllByMember(Member member);

    boolean existsByMemberAndPartner(Member member, Member member1);

    Optional<Friend> findByMemberAndPartner(Member member1, Member member2);

    List<Friend> findAllByMemberAndPartnerStatus(Member member, MemberStatus status);
}
