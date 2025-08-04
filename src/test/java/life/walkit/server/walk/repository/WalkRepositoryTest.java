package life.walkit.server.walk.repository;

import life.walkit.server.member.entity.Member;
import life.walkit.server.member.repository.MemberRepository;
import life.walkit.server.path.entity.Path;
import life.walkit.server.path.repository.PathRepository;
import life.walkit.server.walk.entity.Walk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static life.walkit.server.global.factory.GlobalTestFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class WalkRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    WalkRepository walkRepository;
    @Autowired
    private PathRepository pathRepository;

    Double[][] lineString;

    @BeforeEach
    void setUp() {
        lineString = new Double[][]{
            {126.986, 37.541},
            {126.987, 37.542},
            {126.988, 37.543},
            {126.989, 37.544}
        };

        memberRepository.save(createMember("a@email.com", "회원A"));
        memberRepository.save(createMember("b@email.com", "회원B"));
        memberRepository.save(createMember("c@email.com", "회원C"));
    }

    @Test
    @DisplayName("산책기록 저장 성공 및 조회")
    void saveWalk_success() {
        // given
        Member member = createMember("d@email.com", "회원임");
        Member savedMember = memberRepository.save(member);
        Member foundMember = memberRepository.findByEmail(savedMember.getEmail()).get();
        Path savedPath = pathRepository.save(createPath(lineString));
        Path foundPath = pathRepository.findById(savedPath.getPathId()).get();

        LocalTime startTime = LocalTime.of(0, 0, 0); // startTIme
        LocalTime endTime = LocalTime.of(12, 23, 56); // endTime

        Duration totalTime = Duration.between(startTime, endTime); // 12 hour
        Double totalDistance = 2.0; // 총 거리
        Double pace = 1.2; // 평균 속도 1.2km

        // when
        walkRepository.save(
            createWalk(
                foundMember,
                null,
                foundPath,
                "강남 산책로",
                totalDistance,
                totalTime,
                pace
            )
        );

        // then
        List<Walk> foundWalks = walkRepository.findByMember(foundMember);
        assertThat(foundWalks).hasSize(1);
        assertThat(foundWalks.get(0))
            .satisfies(walk -> {
                assertThat(walk.getMember().getEmail()).isEqualTo("d@email.com");
                assertThat(walk.getPace()).isEqualTo(1.2);
                assertThat(walk.getTotalDistance()).isEqualTo(totalDistance);
            });
    }

    @Test
    @DisplayName("회원별 산책기록 목록 조회")
    void findWalkListByMember_success() {
        // given
        Member member = createMember("d@email.com", "회원임");
        Member savedMember = memberRepository.save(member);
        Member foundMember = memberRepository.findByEmail(savedMember.getEmail()).get();
        Path savePathA = pathRepository.save(createPath(lineString));
        Path savePathB = pathRepository.save(createPath(lineString));
        Path foundPathA = pathRepository.findById(savePathA.getPathId()).get();
        Path foundPathB = pathRepository.findById(savePathB.getPathId()).get();

        // 시간
        LocalTime startTime = LocalTime.of(0, 0, 0);
        LocalTime endTimeA = LocalTime.of(12, 23, 56);
        LocalTime endTimeB = LocalTime.of(11, 23, 56);

        // 총 거리
        Double totalDistanceA = 2.0;
        Double totalDistanceB = 3.0;

        // 시간 계산
        Duration totalTimeA = Duration.between(startTime, endTimeA); // 12 hour
        Duration totalTimeB = Duration.between(startTime, endTimeB); // 11 hour

        // 평균 속도(km)
        Double paceA = 1.2;
        Double paceB = 1.5;

        walkRepository.save(
            createWalk(
                foundMember,
                null,
                foundPathA,
                "수원광교 산책로입니다.",
                totalDistanceA,
                totalTimeA,
                paceA
            )
        );
        walkRepository.save(
            createWalk(
                foundMember,
                null,
                foundPathB,
                "도원동 산책로입니다.",
                totalDistanceB,
                totalTimeB,
                paceB
            )
        );

        // when
        List<Walk> foundWalks = walkRepository.findByMember(foundMember);

        // then
        assertThat(foundWalks).hasSize(2);
        assertThat(foundWalks)
            .extracting("totalDistance", "pace")
            .containsExactlyInAnyOrder(
                tuple(2.0, 1.2),
                tuple(3.0, 1.5)
            );
    }

    @Test
    @DisplayName("산책 기록 삭제")
    void deleteWalk_success() {
        // given
        Member member = createMember("d@email.com", "회원임");
        Member savedMember = memberRepository.save(member);
        Member foundMember = memberRepository.findByEmail(savedMember.getEmail()).get();
        Path savePath = pathRepository.save(createPath(lineString));
        Path foundPath = pathRepository.findById(savePath.getPathId()).get();

        LocalTime startTime = LocalTime.of(0, 0, 0);
        LocalTime endTime = LocalTime.of(12, 23, 56);
        Duration totalTime = Duration.between(startTime, endTime); // 12 hour
        Double totalDistance = 2.0;
        Double pace = 1.2; // 1.2km

        Walk savedWalk = walkRepository.save(
            createWalk(
                foundMember,
                null,
                foundPath,
                "Test Walk Title",
                totalDistance,
                totalTime,
                pace
            )
        );

        List<Walk> walksBeforeDelete = walkRepository.findByMember(foundMember);
        assertThat(walksBeforeDelete).hasSize(1);

        // when
        walkRepository.deleteById(savedWalk.getWalkId());

        // then
        List<Walk> walksAfterDelete = walkRepository.findByMember(foundMember);
        assertThat(walksAfterDelete).isEmpty();
    }

    @Test
    @DisplayName("여러 산책 기록 중 특정 기록만 삭제")
    void deleteSpecificWalk_success() {
        // given
        Member foundMember = memberRepository.findByEmail("a@email.com").get();

        Path savePathA = pathRepository.save(createPath(lineString));
        Path foundPathA = pathRepository.findById(savePathA.getPathId()).get();
        Path savePathB = pathRepository.save(createPath(lineString));
        Path foundPathB = pathRepository.findById(savePathB.getPathId()).get();

        LocalTime startTime = LocalTime.of(0, 0, 0);
        LocalTime endTimeA = LocalTime.of(12, 23, 56);
        LocalTime endTimeB = LocalTime.of(11, 23, 56);
        Duration totalTimeA = Duration.between(startTime, endTimeA); // 12 hour
        Duration totalTimeB = Duration.between(startTime, endTimeB); // 11 hour
        Double paceA = 1.2; // 1.2km 평군 속도
        Double paceB = 1.5; // 1.2km 평군 속도
        Double totalDistance = 3.0; // 총 3km

        Walk walkA = walkRepository.save(
            createWalk(
                foundMember,
                null,
                foundPathA,
                "테스트 타이틀 A",
                totalDistance,
                totalTimeA,
                paceA
            )
        );
        walkRepository.save(
            createWalk(
                foundMember,
                null,
                foundPathB,
                "테스트 타이틀 B",
                totalDistance,
                totalTimeB,
                paceB
            )
        );

        // when
        walkRepository.deleteById(walkA.getWalkId());
        Optional<Walk> deletedWalk = walkRepository.findById(walkA.getWalkId());

        // then
        List<Walk> foundWalk = walkRepository.findByMember(foundMember);
        assertThat(foundWalk).hasSize(1);
        assertThat(foundWalk)
            .extracting("walkTitle")
            .containsExactly("테스트 타이틀 B");


        assertThat(deletedWalk).isEmpty();
    }


}
