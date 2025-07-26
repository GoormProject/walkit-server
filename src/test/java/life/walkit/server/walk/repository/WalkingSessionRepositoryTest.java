package life.walkit.server.walk.repository;

import life.walkit.server.member.entity.Member;
import life.walkit.server.member.repository.MemberRepository;
import life.walkit.server.path.entity.Path;
import life.walkit.server.path.repository.PathRepository;
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

import static life.walkit.server.global.factory.GlobalTestFactory.*;
import static org.assertj.core.api.Assertions.assertThat;

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

    Walk walk;
    @Autowired
    private PathRepository pathRepository;

    @BeforeEach
    void setUp() {
        Double[][] lineString = new Double[][]{
            {126.986, 37.541},
            {126.987, 37.542},
            {126.988, 37.543},
            {126.989, 37.544}
        };
        Path savePath = pathRepository.save(createPath(lineString));

        Member member = memberRepository.save(createMember("a@email.com", "회원A"));
        walk = walkRepository.save(
                createWalk(
                        member,
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        LocalDate.now(),
                        savePath,
                        null,
                        null
                )
        );

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
        Long sessionId = saveSession.getEventId();

        // when
        saveSession.updateWalkingSessionEventType(EventType.RESUME);

        // then
        WalkingSession updatedSession = walkingSessionRepository.findById(sessionId).get();
        assertThat(updatedSession.getEventType())
                .isEqualTo(EventType.RESUME);
    }


}
