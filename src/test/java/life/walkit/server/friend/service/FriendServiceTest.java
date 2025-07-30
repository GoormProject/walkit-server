package life.walkit.server.friend.service;

import static life.walkit.server.global.factory.GlobalTestFactory.createFriendRequest;
import static life.walkit.server.global.factory.GlobalTestFactory.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import life.walkit.server.friend.dto.FriendRequestResponseDTO;
import life.walkit.server.friend.dto.FriendResponseDTO;
import life.walkit.server.friend.dto.ReceivedFriendResponse;
import life.walkit.server.friend.dto.SentFriendResponse;
import life.walkit.server.friend.entity.Friend;
import life.walkit.server.friend.entity.FriendRequest;
import life.walkit.server.friend.enums.FriendRequestStatus;
import life.walkit.server.friend.error.FriendErrorCode;
import life.walkit.server.friend.error.FriendException;
import life.walkit.server.friend.repository.FriendRepository;
import life.walkit.server.friend.repository.FriendRequestRepository;
import life.walkit.server.global.util.GeoUtils;
import life.walkit.server.member.dto.LocationDto;
import life.walkit.server.member.entity.Member;
import life.walkit.server.member.error.MemberException;
import life.walkit.server.member.error.enums.MemberErrorCode;
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
public class FriendServiceTest {

    @Autowired
    private FriendService friendService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private FriendRequestRepository friendRequestRepository;

    private Member memberA;
    private Member memberB;
    private Member memberC;

    @BeforeEach
    void setUp() {
        memberA = memberRepository.save(createMember("a@email.com", "회원A"));
        memberB = memberRepository.save(createMember("b@email.com", "회원B"));
        memberC = memberRepository.save(createMember("c@email.com", "회원C"));
    }

    @AfterEach
    void tearDown() {
        friendRequestRepository.deleteAll();
        friendRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @Transactional
    @DisplayName("친구 요청 성공")
    void sendFriendRequest_success() {
        FriendRequestResponseDTO response = friendService.sendFriendRequest(memberA.getMemberId(), memberB.getNickname());

        assertThat(response.status()).isEqualTo(FriendRequestStatus.PENDING);
        assertThat(response.senderNickname()).isEqualTo("회원A");
        assertThat(response.receiverNickname()).isEqualTo("회원B");

        List<FriendRequest> savedRequests = friendRequestRepository.findByReceiverAndStatus(memberB, FriendRequestStatus.PENDING);
        assertThat(savedRequests).hasSize(1);
        FriendRequest savedRequest = savedRequests.get(0);
    }

    @Test
    @Transactional
    @DisplayName("내가 보낸 친구 요청 목록 조회 성공")
    void getSentFriendRequests_success() {
        Member receiver1 = memberRepository.save(createMember("receiver1@email.com", "수신자1"));
        Member receiver2 = memberRepository.save(createMember("receiver2@email.com", "수신자2"));

        friendRequestRepository.save(createFriendRequest(memberA, receiver1));
        friendRequestRepository.save(createFriendRequest(memberA, receiver2));

        List<SentFriendResponse> responses = friendService.getSentFriendRequests(memberA.getMemberId());

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting("receiverNickname")
                .containsExactlyInAnyOrder("수신자1", "수신자2");
    }

    @Test
    @Transactional
    @DisplayName("내가 받은 친구 요청 목록 조회 성공")
    void getReceivedFriendRequests_success() {
        friendRequestRepository.save(createFriendRequest(memberB, memberA)); // B -> A
        friendRequestRepository.save(createFriendRequest(memberC, memberA)); // C -> A

        List<ReceivedFriendResponse> responses = friendService.getReceivedFriendRequests(memberA.getMemberId());

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting("senderNickname")
                .containsExactlyInAnyOrder("회원B", "회원C");

        assertThat(responses).extracting("requestStatus")
                .containsOnly(FriendRequestStatus.PENDING);
    }

    @Test
    @Transactional
    @DisplayName("내가 받은 친구 요청 목록 조회 - 요청이 없는 경우")
    void getReceivedFriendRequests_empty_success() {
        List<ReceivedFriendResponse> responses = friendService.getReceivedFriendRequests(memberA.getMemberId());
        assertThat(responses).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("친구 요청 승인 성공")
    void approveFriendRequest_success() {
        FriendRequest friendRequest = friendRequestRepository.save(createFriendRequest(memberB, memberA));

        friendService.approveFriendRequest(friendRequest.getFriendRequestId(), memberA.getMemberId());

        assertThat(friendRequest.getStatus()).isEqualTo(FriendRequestStatus.APPROVED);

        assertThat(friendRepository.existsByMemberAndPartner(memberA, memberB)).isTrue();
        assertThat(friendRepository.existsByMemberAndPartner(memberB, memberA)).isTrue();
    }

    @Test
    @DisplayName("친구 요청 승인 실패 - 요청이 존재하지 않음")
    void approveFriendRequest_fail_requestNotFound() {
        Long invalidRequestId = 999L;

        assertThatThrownBy(() -> friendService.approveFriendRequest(invalidRequestId, memberA.getMemberId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(FriendErrorCode.FRIEND_REQUEST_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("친구 요청 승인 실패 - 승인자가 요청의 수신자가 아님")
    void approveFriendRequest_fail_unauthorizedApprover() {
        FriendRequest friendRequest = friendRequestRepository.save(createFriendRequest(memberB, memberA));

        assertThatThrownBy(() -> friendService.approveFriendRequest(friendRequest.getFriendRequestId(), memberB.getMemberId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(FriendErrorCode.UNAUTHORIZED_APPROVER.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("친구 요청 거절 성공")
    void rejectFriendRequest_success() {
        FriendRequest friendRequest = friendRequestRepository.save(createFriendRequest(memberB, memberA)); // B -> A

        friendService.rejectFriendRequest(friendRequest.getFriendRequestId(), memberA.getMemberId());

        assertThat(friendRequestRepository.findById(friendRequest.getFriendRequestId())).isEmpty();
    }

    @Test
    @DisplayName("친구 요청 거절 실패 - 요청이 존재하지 않음")
    void rejectFriendRequest_fail_requestNotFound() {
        Long invalidRequestId = 999L;

        assertThatThrownBy(() -> friendService.rejectFriendRequest(invalidRequestId, memberA.getMemberId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(FriendErrorCode.FRIEND_REQUEST_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("친구 요청 거절 실패 - 거절자가 요청의 수신자가 아님")
    void rejectFriendRequest_fail_unauthorizedRejecter() {
        FriendRequest friendRequest = friendRequestRepository.save(createFriendRequest(memberB, memberA));

        assertThatThrownBy(() -> friendService.rejectFriendRequest(friendRequest.getFriendRequestId(), memberB.getMemberId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(FriendErrorCode.UNAUTHORIZED_APPROVER.getMessage());
    }

    @Test
    @DisplayName("친구 요청 거절 실패 - 이미 승인된 요청")
    void rejectFriendRequest_fail_alreadyApproved() {
        FriendRequest friendRequest = friendRequestRepository.save(createFriendRequest(memberB, memberA));
        friendRequest.approve();
        friendRequestRepository.save(friendRequest);

        assertThatThrownBy(() -> friendService.rejectFriendRequest(friendRequest.getFriendRequestId(), memberA.getMemberId()))
                .isInstanceOf(FriendException.class)
                .hasMessage(FriendErrorCode.FRIEND_STATUS_INVALID.getMessage());
    }


    @Test
    @DisplayName("친구 목록 조회 성공")
    void getFriendList_success() {
        // Arrange - 테스트 데이터 생성 및 저장
        Member memberA = memberRepository.save(createMember("user01@gmail.com", "User01"));
        Member memberB = memberRepository.save(createMember("user02@gmail.com", "User02"));
        Member memberC = memberRepository.save(createMember("user03@gmail.com", "User03"));

        memberB.updateLocation(GeoUtils.toPoint(126.9780, 37.5665)); // 서울
        memberC.updateLocation(GeoUtils.toPoint(129.0756, 35.1796)); // 부산

        memberRepository.save(memberB);
        memberRepository.save(memberC);

        friendRepository.save(Friend.builder().member(memberA).partner(memberB).build());
        friendRepository.save(Friend.builder().member(memberA).partner(memberC).build());

        List<FriendResponseDTO> friends = friendService.getFriends(memberA.getMemberId());

        assertThat(friends).hasSize(2);

        // 친구 User02 검증
        FriendResponseDTO friendB = friends.stream()
                .filter(f -> f.getNickname().equals("User02"))
                .findFirst()
                .orElseThrow();
        assertThat(friendB.getLastLocation().getLat()).isEqualTo(37.5665);
        assertThat(friendB.getLastLocation().getLng()).isEqualTo(126.9780);

        // 친구 User03 검증
        FriendResponseDTO friendC = friends.stream()
                .filter(f -> f.getNickname().equals("User03"))
                .findFirst()
                .orElseThrow();
        assertThat(friendC.getLastLocation().getLat()).isEqualTo(35.1796);
        assertThat(friendC.getLastLocation().getLng()).isEqualTo(129.0756);
    }

//    @Test
//    @DisplayName("친구 목록 조회 테스트")
//    void getFriends_success() {
//        // given
//        Member member = memberRepository.findByEmail("test@test.com")
//                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
//
//        // when
//        List<FriendResponseDTO> friends = friendService.getFriends(member.getMemberId());
//
//        // then
//        assertNotNull(friends);
//        assertFalse(friends.isEmpty()); // 친구가 비어있지 않은지 확인
//        assertEquals("친구 닉네임", friends.get(0).getNickname());
//    }







}
