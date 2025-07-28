package life.walkit.server.friend.service;

import static life.walkit.server.global.factory.GlobalTestFactory.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import life.walkit.server.friend.dto.FriendRequestResponseDTO;
import life.walkit.server.friend.entity.FriendRequest;
import life.walkit.server.friend.enums.FriendRequestStatus;
import life.walkit.server.friend.repository.FriendRepository;
import life.walkit.server.friend.repository.FriendRequestRepository;
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

    @BeforeEach
    void setUp() {
        memberA = memberRepository.save(createMember("a@email.com", "회원A"));
        memberB = memberRepository.save(createMember("b@email.com", "회원B"));
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
        // when
        FriendRequestResponseDTO response = friendService.sendFriendRequest(memberA.getMemberId(), memberB.getNickname());

        // then
        assertThat(response.status()).isEqualTo(FriendRequestStatus.PENDING);
        assertThat(response.senderNickname()).isEqualTo("회원A");
        assertThat(response.receiverNickname()).isEqualTo("회원B");

        List<FriendRequest> savedRequests = friendRequestRepository.findByReceiverAndStatus(memberB, FriendRequestStatus.PENDING);
        assertThat(savedRequests).hasSize(1);
        FriendRequest savedRequest = savedRequests.get(0);
    }







}
