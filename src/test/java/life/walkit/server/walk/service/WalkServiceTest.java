package life.walkit.server.walk.service;

import life.walkit.server.member.entity.Member;
import life.walkit.server.member.repository.MemberRepository;
import life.walkit.server.path.repository.PathRepository;
import life.walkit.server.walk.dto.request.WalkRequest;
import life.walkit.server.walk.dto.response.WalkEventResponse;
import life.walkit.server.walk.entity.Walk;
import life.walkit.server.walk.entity.WalkingSession;
import life.walkit.server.walk.entity.enums.EventType;
import life.walkit.server.walk.error.enums.WalkErrorCode;
import life.walkit.server.walk.error.enums.WalkException;
import life.walkit.server.walk.repository.WalkRepository;
import life.walkit.server.walk.repository.WalkingSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static life.walkit.server.global.factory.GlobalTestFactory.createMember;
import static life.walkit.server.global.factory.GlobalTestFactory.createWalk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Autowired
    PathRepository pathRepository;

    Member member;
    Walk walk;
    @Autowired
    private WalkingSessionRepository walkingSessionRepository;

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
    void pauseWalk_success() {
        WalkEventResponse walkEventResponse = walkService.startWalk(member.getMemberId()); // 산책 기록 생성
        WalkEventResponse walkEventResponsePause = walkService.pauseWalk(walkEventResponse.walkId());

        assertThat(walkEventResponsePause.eventType()).isEqualTo(EventType.PAUSE);
        assertThat(walkEventResponsePause.eventId()).isNotNull();
        assertThat(walkEventResponsePause.walkId()).isNotNull();
    }

    @Test
    @DisplayName("산책 기록 재개 성공")
    void resumeWalk_success() {
        WalkEventResponse walkEventResponse = walkService.startWalk(member.getMemberId()); // 산책 기록 생성
        WalkEventResponse walkEventResponsePause = walkService.pauseWalk(walkEventResponse.walkId()); // 산책 기록 멈춤
        WalkEventResponse walkEventResponseResume = walkService.resumeWalk(walkEventResponsePause.walkId()); // 산책 기록 재개

        assertThat(walkEventResponseResume.eventType()).isEqualTo(EventType.RESUME);
        assertThat(walkEventResponseResume.eventId()).isNotNull();
        assertThat(walkEventResponseResume.walkId()).isNotNull();
    }

    @Test
    @DisplayName("산책 기록이 멈춘 상태가 아닐시 에러")
    void resumeWalk_notPausedState_throwsException() {
        // given
        WalkEventResponse walkEventResponse = walkService.startWalk(member.getMemberId()); // 산책 기록 생성

        // when
        WalkException walkException = assertThrows(WalkException.class, () ->
            walkService.resumeWalk(walkEventResponse.walkId())
        );

        // then
        assertThat(walkException.getErrorCode()).isEqualTo(WalkErrorCode.WALK_NOT_PAUSED);
    }

    @Test
    @DisplayName("산책 기록 종료 성공")
    void endWalk_success() {
        // given
        WalkEventResponse walkEventResponse = walkService.startWalk(member.getMemberId());
        WalkEventResponse walkEventResponseEnd = walkService.endWalk(walkEventResponse.walkId());

        // when & then
        assertThat(walkEventResponseEnd.eventType()).isEqualTo(EventType.END);
        assertThat(walkEventResponseEnd.eventId()).isNotNull();
        assertThat(walkEventResponseEnd.walkId()).isNotNull();
    }

    @Test
    @DisplayName("산책 기록이 이미 멈춤 상태일시 에러")
    void resumeWalk_alreadyPaused_throwsException() {
        // given
        WalkEventResponse walkEventResponse = walkService.startWalk(member.getMemberId()); // 산책 기록 생성
        WalkEventResponse walkEventResponseEnd = walkService.endWalk(walkEventResponse.walkId());

        // when
        WalkException walkException = assertThrows(WalkException.class, () ->
            walkService.endWalk(walkEventResponseEnd.walkId())
        );

        // then
        assertThat(walkException.getErrorCode()).isEqualTo(WalkErrorCode.WALK_ALREADY_COMPLETED);
    }

    @Test
    @DisplayName("산책 기록 저장 성공")
    void createWalk_success() {
        // given
        List<List<Double>> path = List.of(
            List.of(126.75791835403612, 37.662510637017874),
            List.of(126.75790151956403, 37.66262761454681),
            List.of(126.75789029658108, 37.662723861742975),
            List.of(126.75790900155192, 37.66283343532169),
            List.of(126.75795763447428, 37.662935605134706),
            List.of(126.75809792174988, 37.66306886989648),
            List.of(126.75818770560676, 37.66312069501714)
        );

        List<Double> startPoint = List.of(
            126.75791835403612, 37.662510637017874
        );

        WalkEventResponse walkEventResponse = walkService.startWalk(member.getMemberId()); // 기록 생성
        WalkRequest walkRequest = new WalkRequest(
            walkEventResponse.walkId(),
            "2025-07-28의 산책기록",
            3600,
            5635.2534,
            5.636,
            path,
            startPoint,
            walkEventResponse.eventId(),
            "END",
            "example.test.url"
        );

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Coordinate[] coordinates = path.stream()
            .map(p -> new Coordinate(p.get(0), p.get(1)))
            .toArray(Coordinate[]::new);
        LineString lineString = geometryFactory.createLineString(coordinates);
        Point point = geometryFactory.createPoint(new Coordinate(startPoint.get(0), startPoint.get(1)));

        // when
        walkService.createWalk(walkRequest);
        Walk foundWalk = walkRepository.findById(walkEventResponse.walkId()).get();
        WalkingSession walkingSession = walkingSessionRepository.findFirstByWalkOrderByEventTimeDesc(foundWalk).get();

        // then
        assertThat(foundWalk.getTotalDistance()).isEqualTo(5635.2534);
        assertThat(foundWalk.getPace()).isEqualTo(5.636);
        assertThat(foundWalk.getTotalTime().getSeconds()).isEqualTo(3600);
        assertThat(walkingSession.getEventType()).isEqualTo(EventType.END);
        assertThat(foundWalk.getPath().getPath()).isEqualTo(lineString);
        assertThat(foundWalk.getPath().getPoint()).isEqualTo(point);
        assertThat(foundWalk.getWalkTitle()).isEqualTo("2025-07-28의 산책기록");
    }

}
