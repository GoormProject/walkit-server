package life.walkit.server.friend.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static life.walkit.server.global.factory.GlobalTestFactory.*;

import life.walkit.server.friend.entity.Friend;
import life.walkit.server.friend.entity.FriendRequest;
import life.walkit.server.friend.enums.FriendRequestStatus;
import life.walkit.server.member.entity.Member;
import life.walkit.server.member.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class FriendRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @BeforeEach
    void setUp() {
        memberRepository.save(createMember("a@email.com", "회원A"));
        memberRepository.save(createMember("b@email.com", "회원B"));
        memberRepository.save(createMember("c@email.com", "회원C"));
    }

    @AfterEach
    void tearDown() {
        friendRequestRepository.deleteAll();
        friendRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @Transactional
    @DisplayName("친구 요청 후 승인 및 친구 저장 성공")
    void saveFriendAfterApproval_success() {
        // given
        Member memberA = memberRepository.findByNickname("회원A").get();
        Member memberB = memberRepository.findByNickname("회원B").get();

        // 친구 요청 생성 및 DB 저장
        FriendRequest friendRequest = friendRequestRepository.save(createFriendRequest(memberA, memberB));

        friendRequest.approve(); // 친구 요청 승인
        friendRequest = friendRequestRepository.save(friendRequest); // 상태 변경 후 저장

        // when
        Friend friend = friendRepository.save(createFriend(memberA, memberB));

        // then
        Friend foundFriend = friendRepository.findById(friend.getFriendId())
                .orElseThrow(() -> new IllegalArgumentException("Friend not found"));
        assertThat(foundFriend).isNotNull();
        assertThat(foundFriend.getMember().getNickname()).isEqualTo("회원A");
        assertThat(foundFriend.getPartner().getNickname()).isEqualTo("회원B");
    }

    @Test
    @Transactional
    @DisplayName("회원의 친구 목록 조회 성공")
    void findByMember_success() {
        // given
        Member memberA = memberRepository.findByNickname("회원A").get();
        Member memberB = memberRepository.findByNickname("회원B").get();
        Member memberC = memberRepository.findByNickname("회원C").get();

        friendRepository.save(createFriend(memberA, memberB));
        friendRepository.save(createFriend(memberA, memberC));

        // when
        List<Friend> friends = friendRepository.findAllByMember(memberA);

        // then
        assertThat(friends).hasSize(2)
                .extracting(friend -> friend.getPartner().getNickname())
                .containsExactlyInAnyOrder("회원B", "회원C");
    }

    @Test
    @Transactional
    @DisplayName("수락 대기 중인 친구 요청 조회 성공")
    void findPendingFriendRequests_success() {
        // given
        Member memberA = memberRepository.findByNickname("회원A").get();
        Member memberB = memberRepository.findByNickname("회원B").get();
        Member memberC = memberRepository.findByNickname("회원C").get();

        FriendRequest friendRequestA = FriendRequest.builder()
                .sender(memberA)
                .receiver(memberC)
                .build();
        friendRequestRepository.save(friendRequestA);

        FriendRequest friendRequestB = FriendRequest.builder()
                .sender(memberB)
                .receiver(memberC)
                .build();

        friendRequestRepository.save(friendRequestB);

        // when
        List<FriendRequest> pendingRequests = friendRequestRepository.findByReceiverAndStatus(memberC, FriendRequestStatus.PENDING);

        // then
        assertThat(pendingRequests).hasSize(2)
                .extracting(request -> request.getSender().getNickname())
                .containsExactlyInAnyOrder("회원A", "회원B");
        assertThat(pendingRequests).allMatch(request -> request.getStatus() == FriendRequestStatus.PENDING);
    }
}
