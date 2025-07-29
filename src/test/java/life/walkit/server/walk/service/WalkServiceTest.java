package life.walkit.server.walk.service;

import life.walkit.server.member.entity.Member;
import life.walkit.server.member.repository.MemberRepository;
import life.walkit.server.walk.dto.response.WalkEventResponse;
import life.walkit.server.walk.entity.enums.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static life.walkit.server.global.factory.GlobalTestFactory.createMember;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class WalkServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    WalkService walkService;

    Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(createMember("a@email.com", "회원A"));
    }

    @Test
    @DisplayName("산책 기록 시작 성공")
    void startWalk_success() {
        WalkEventResponse walkEventResponse = walkService.startWalk(member.getMemberId());

        assertThat(walkEventResponse.eventType()).isEqualTo(EventType.START);
        assertThat(walkEventResponse.eventId()).isNotNull();
        assertThat(walkEventResponse.walkId()).isNotNull();
    }


}
