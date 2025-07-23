package life.walkit.server.member.repository;

import life.walkit.server.member.entity.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static life.walkit.server.member.factory.MemberTestFactory.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.save(createMember("test@email.com", "테스트회원"));
        memberRepository.save(createMember("a@email.com", "검색A"));
        memberRepository.save(createMember("b@email.com", "검색B"));
    }

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("멤버 저장 성공")
    void save_success() {
        // given
        Member member = createMember("email@email.com", "닉네임");

        // when
        Member savedMember = memberRepository.save(member);

        // then
        Member foundMember = memberRepository.findById(savedMember.getMemberId()).get();
        assertThat(foundMember)
                .extracting("email", "nickname")
                .containsExactly("email@email.com", "닉네임");
    }

    @Test
    @DisplayName("email로 멤버 조회 성공")
    void findByEmail_success() {
        // when & then
        Member foundMember = memberRepository.findByEmail("test@email.com").get();
        assertThat(foundMember)
                .extracting("email", "nickname")
                .containsExactly("test@email.com", "테스트회원");
    }

    @Test
    @DisplayName("nickname으로 멤버 조회 성공")
    void findByNickname_success() {
        // when & then
        Member foundMember = memberRepository.findByNickname("테스트회원").get();
        assertThat(foundMember)
                .extracting("email", "nickname")
                .containsExactly("test@email.com", "테스트회원");
    }

    @Test
    @DisplayName("nickname으로 멤버 목록 검색 성공")
    void findByNicknameContaining_success() {
        // when
        List<Member> foundMembers = memberRepository.findByNicknameContaining("검색");

        // then
        assertThat(foundMembers)
                .extracting("nickname")
                .containsExactlyInAnyOrder("검색A", "검색B");
    }

}
