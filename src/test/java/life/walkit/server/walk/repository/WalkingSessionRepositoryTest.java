package life.walkit.server.walk.repository;

import life.walkit.server.member.entity.Member;
import life.walkit.server.member.repository.MemberRepository;
import life.walkit.server.trail.entity.Trail;
import life.walkit.server.trail.repository.TrailRepository;
import life.walkit.server.walk.entity.Walk;
import life.walkit.server.walk.entity.WalkingSession;
import life.walkit.server.walk.entity.enums.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static life.walkit.server.global.factory.GlobalTestFactory.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class WalkingSessionRepositoryTest {

    @Autowired
    WalkRepository walkRepository;

    @Autowired
    WalkingSessionRepository walkingSessionRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TrailRepository trailRepository;

    Walk walk;

    @BeforeEach
    void setUp() {
        memberRepository.save(createMember("a@email.com", "회원A"));
        Member foundMember = memberRepository.findByEmail("a@email.com").get();
        trailRepository.save(createTrail(foundMember, "해운대구", "해운대구 근처 산책로 입니다.", 3.2));
        Trail foundTrail = trailRepository.findByMember(foundMember).get(0);
        walkRepository.save(createWalk(foundMember, foundTrail, LocalDateTime.now(), LocalDateTime.now(), LocalDate.now(), null, null));
        walk = walkRepository.findByMember(foundMember).get(0);
    }

    @Test
    @DisplayName("산책 기록 중간 세션 한개 저장 성공")
    void walkingSession_success() {
        // given
        walkingSessionRepository.save(createWalkingSession(walk, EventType.PAUSE));

        // when
        List<WalkingSession> foundWalkList = walkingSessionRepository.findByWalk(walk);

        // then
        assertThat(foundWalkList)
                .hasSize(1)
                .extracting("eventType")
                .containsExactly(EventType.PAUSE);

    }

    @Test
    @DisplayName("산책 기록 중간 세션 상태 변경 성공")
    void walkingSessionStateChange_success() {
        // given
        WalkingSession saveSession = walkingSessionRepository.save(createWalkingSession(walk, EventType.PAUSE));

        // when
        saveSession.updateWalkingSessionEventType(EventType.RESUME);

        // then
        assertThat(saveSession.getEventType())
                .isEqualTo(EventType.RESUME);
    }


}
