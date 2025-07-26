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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static life.walkit.server.global.factory.GlobalTestFactory.*;
import static org.assertj.core.api.Assertions.assertThat;

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

        // when
        LocalDateTime startTime = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        walkRepository.save(
            createWalk(
                foundMember,
                null,
                startTime,
                null,
                 today,
                foundPath,
                null,
                null
            )
        );

        // then
        List<Walk> foundWalks = walkRepository.findByMember(foundMember);
        assertThat(foundWalks).hasSize(1);
        assertThat(foundWalks.get(0))
                .satisfies(walk -> {
                    assertThat(walk.getMember().getEmail()).isEqualTo("d@email.com");
                    assertThat(walk.getStartedAt()).isEqualTo(startTime);
                    assertThat(walk.getDate()).isEqualTo(today);
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

        // 산책 기록 2개 생성 (각각 다른 날짜와 시간)
        LocalDateTime startTimeA = LocalDateTime.of(2025, 7, 20, 10, 0);
        LocalDateTime startTimeB = LocalDateTime.of(2025, 7, 21, 15, 30);

        walkRepository.save(
            createWalk(
                foundMember,
                null,
                startTimeA,
                startTimeA.plusHours(1),
                LocalDate.of(
                    2025,
                    7,
                    20
                ),
                foundPathA,
                null,
                null
            )
        );
        walkRepository.save(
            createWalk(
                foundMember,
                null,
                startTimeB,
                startTimeB.plusMinutes(45),
                LocalDate.of(
                    2025,
                    7,
                    21),
                foundPathB,
                null,
                null
            )
        );

        // when
        List<Walk> foundWalks = walkRepository.findByMember(foundMember);

        // then
        assertThat(foundWalks).hasSize(2);
        assertThat(foundWalks)
            .extracting("date")
            .containsExactlyInAnyOrder(
                LocalDate.of(2025, 7, 20),
                LocalDate.of(2025, 7, 21)
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

        LocalDateTime startTime = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        Walk savedWalk = walkRepository.save(
            createWalk(
                foundMember,
                null,
                startTime,
                null,
                today,
                foundPath,
                null,
                null
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

        LocalDateTime startTimeA = LocalDateTime.of(2025, 7, 20, 10, 0);
        LocalDateTime startTimeB = LocalDateTime.of(2025, 7, 21, 15, 30);

        Walk walkA = walkRepository.save(
            createWalk(
                foundMember,
                null,
                startTimeA,
                startTimeA.plusHours(1),
                LocalDate.of(
                    2025,
                    7,
                    20
                ),
                foundPathA,
                null,
                null
            )
        );
        walkRepository.save(
            createWalk(
                foundMember,
                null,
                startTimeB,
                startTimeB.plusMinutes(45),
                LocalDate.of(
                    2025,
                    7,
                    21
                ),
                foundPathB,
                null,
                null
            )
        );

        // when
        walkRepository.deleteById(walkA.getWalkId());
        Optional<Walk> deletedWalk = walkRepository.findById(walkA.getWalkId());

        // then
        List<Walk> foundWalk = walkRepository.findByMember(foundMember);
        assertThat(foundWalk).hasSize(1);
        assertThat(foundWalk)
                .extracting("date")
                .containsExactly(
                    LocalDate.of(
                        2025,
                        7,
                        21
                    )
                );

        assertThat(deletedWalk).isEmpty();
    }


}
