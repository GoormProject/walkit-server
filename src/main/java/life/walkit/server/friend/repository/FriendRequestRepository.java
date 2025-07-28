package life.walkit.server.friend.repository;

import life.walkit.server.friend.entity.FriendRequest;
import life.walkit.server.friend.enums.FriendRequestStatus;
import life.walkit.server.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findByReceiverAndStatus(Member receiver, FriendRequestStatus status);

    // 친구 요청 시 예외처리 로직
    boolean existsBySenderAndReceiver(Member sender, Member receiver);

    // 내가 보낸 친구 요청을 조회하기 위한 메서드
    List<FriendRequest> findBySender(Member sender);
}
