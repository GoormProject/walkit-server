package life.walkit.server.friendrequest.repository;

import life.walkit.server.friendrequest.entity.FriendRequest;
import life.walkit.server.friendrequest.entity.enums.FriendRequestStatus;
import life.walkit.server.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findByReceiverAndStatus(Member receiver, FriendRequestStatus status);

    // 친구 요청 시 예외처리 로직
    boolean existsBySenderAndReceiver(Member sender, Member receiver);
}
