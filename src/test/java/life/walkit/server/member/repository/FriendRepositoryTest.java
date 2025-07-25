package life.walkit.server.member.repository;

import life.walkit.server.friend.repository.FriendRepository;
import life.walkit.server.friend.entity.Friend;
import life.walkit.server.member.entity.Member;
import life.walkit.server.friend.entity.FriendStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static life.walkit.server.global.factory.GlobalTestFactory.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class FriendRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private FriendRepository friendRepository;

    @BeforeEach
    void setUp() {
        memberRepository.save(createMember("a@email.com", "회원A"));
        memberRepository.save(createMember("b@email.com", "회원B"));
        memberRepository.save(createMember("c@email.com", "회원C"));
    }

    @AfterEach
    void tearDown() {
        friendRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("친구 저장 성공")
    void save_success() {
        // given
        Member memberA = memberRepository.findByNickname("회원A").get();
        Member memberB = memberRepository.findByNickname("회원B").get();

        // when
        Friend friendAB = friendRepository.save(createFriend(memberA, memberB));

        // then
        Friend foundFriendAB = friendRepository.findById(friendAB.getFriendId()).get();
        assertThat(foundFriendAB)
                .extracting("status")
                .isEqualTo(FriendStatus.PENDING);
    }

    @Test
    @Transactional
    @DisplayName("회원의 친구 목록 조회 성공")
    void findByMemberAndStatus_success() {
        // given
        Member memberA = memberRepository.findByNickname("회원A").get();
        Member memberB = memberRepository.findByNickname("회원B").get();
        Member memberC = memberRepository.findByNickname("회원C").get();

        friendRepository.save(createFriendApproved(memberA, memberC));
        friendRepository.save(createFriendApproved(memberC, memberA));
        friendRepository.save(createFriendApproved(memberB, memberC));
        friendRepository.save(createFriendApproved(memberC, memberB));

        // when
        List<Friend> approvedFriends = friendRepository.findByMemberAndStatus(memberC, FriendStatus.APPROVED);

        // then
        assertThat(approvedFriends).hasSize(2)
                .extracting(friend -> friend.getPartner().getNickname())
                .containsExactlyInAnyOrder("회원A", "회원B");
    }

    @Test
    @Transactional
    @DisplayName("수락/거절 대기 중인 친구 조회 성공")
    void findByPartnerAndStatus_success() {
        // given
        Member memberA = memberRepository.findByNickname("회원A").get();
        Member memberB = memberRepository.findByNickname("회원B").get();
        Member memberC = memberRepository.findByNickname("회원C").get();

        friendRepository.save(createFriend(memberA, memberC));
        friendRepository.save(createFriend(memberB, memberC));

        // when
        List<Friend> pendingFriends = friendRepository.findByPartnerAndStatus(memberC, FriendStatus.PENDING);

        // then
        assertThat(pendingFriends).hasSize(2)
                .extracting(friend -> friend.getMember().getNickname())
                .containsExactlyInAnyOrder("회원A", "회원B");
    }
}
