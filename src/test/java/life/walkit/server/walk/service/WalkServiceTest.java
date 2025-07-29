package life.walkit.server.walk.service;

import life.walkit.server.member.entity.Member;
import life.walkit.server.member.repository.MemberRepository;
import life.walkit.server.walk.dto.enums.WalkResponse;
import life.walkit.server.walk.dto.response.WalkEventResponse;
import life.walkit.server.walk.entity.Walk;
import life.walkit.server.walk.entity.enums.EventType;
import life.walkit.server.walk.repository.WalkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static life.walkit.server.global.factory.GlobalTestFactory.createMember;
import static life.walkit.server.global.factory.GlobalTestFactory.createWalk;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class WalkServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    WalkRepository walkRepository;

    @Autowired
    WalkService walkService;

    Member member;
    Walk walk;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(createMember("a@email.com", "회원A"));
        Walk walk = walkRepository.save(
            createWalk(
                member,
                null,
                null,
                null,
                null,
                null,
                null,
                false
            )
        );
    }

    @Test
    @DisplayName("산책 기록 시작 성공")
    void startWalk_success() {
        WalkEventResponse walkEventResponse = walkService.startWalk(member.getMemberId());

        assertThat(walkEventResponse.eventType()).isEqualTo(EventType.START);
        assertThat(walkEventResponse.eventId()).isNotNull();
        assertThat(walkEventResponse.walkId()).isNotNull();
    }

    @Test
    @DisplayName("산책 기록 멈춤 성공")
    void paseWalk_success() {
        WalkEventResponse walkEventResponse = walkService.startWalk(member.getMemberId()); // 산책 기록 생성
        WalkEventResponse walkEventResponsePause = walkService.pauseWalk(walkEventResponse.walkId());

        assertThat(walkEventResponsePause.eventType()).isEqualTo(EventType.PAUSE);
        assertThat(walkEventResponsePause.eventId()).isNotNull();
        assertThat(walkEventResponsePause.walkId()).isNotNull();
    }


}
