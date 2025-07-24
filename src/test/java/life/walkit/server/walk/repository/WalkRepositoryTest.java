package life.walkit.server.walk.repository;

import life.walkit.server.member.entity.Member;
import life.walkit.server.member.repository.MemberRepository;
import life.walkit.server.trail.entity.Trail;
import life.walkit.server.trail.repository.TrailRepository;
import life.walkit.server.walk.entity.Walk;
import org.junit.jupiter.api.AfterEach;
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
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class WalkRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TrailRepository trailRepository;

    @Autowired
    WalkRepository walkRepository;

    @BeforeEach
    void setUp() {
        memberRepository.save(createMember("a@email.com", "회원A"));
        memberRepository.save(createMember("b@email.com", "회원B"));
        memberRepository.save(createMember("c@email.com", "회원C"));

        Member memberA = memberRepository.findByEmail("a@email.com").get();
        Member memberB = memberRepository.findByEmail("b@email.com").get();
        Member memberC = memberRepository.findByEmail("c@email.com").get();

        trailRepository.save(createTrail(memberA, "인사동", "인사동 근처 산책로 입니다.", 3.2));
        trailRepository.save(createTrail(memberB, "강남구", "강남구 근처 산책로 입니다.", 2.2));
        trailRepository.save(createTrail(memberC, "도원동", "도원동 근처 산책로 입니다.", 1.2));
    }

    @Test
    @DisplayName("산책기록 저장 성공 및 조회")
    void saveWalk_success() {
        // given
        Member member = createMember("d@email.com", "회원임");
        Member savedMember = memberRepository.save(member);
        Member foundMember = memberRepository.findByEmail(savedMember.getEmail()).get();
        Trail savedTrail = trailRepository.save(createTrail(foundMember, "해운대구", "해운대구 근처 산책로 입니다.", 3.2));
        List<Trail> trailList = trailRepository.findByMember(savedTrail.getMember());
        Trail foundTrail = trailList.get(0);

        // when
        LocalDateTime startTime = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        walkRepository.save(createWalk(foundMember, foundTrail, startTime, null, today, null, null));

        // then
        List<Walk> foundWalks = walkRepository.findByMember(foundMember);
        assertThat(foundWalks).hasSize(1);
        assertThat(foundWalks.get(0))
                .satisfies(walk -> {
                    assertThat(walk.getMember().getEmail()).isEqualTo("d@email.com");
                    assertThat(walk.getTrail().getTitle()).isEqualTo("해운대구");
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

        // 산책로 2개 생성
        Trail savedTrailA = trailRepository.save(createTrail(foundMember, "해운대구", "해운대구 산책로", 3.2));
        Trail savedTrailB = trailRepository.save(createTrail(foundMember, "광안리", "광안리 산책로", 2.5));

        // 산책 기록 2개 생성 (각각 다른 날짜와 시간)
        LocalDateTime startTimeA = LocalDateTime.of(2025, 7, 20, 10, 0);
        LocalDateTime startTimeB = LocalDateTime.of(2025, 7, 21, 15, 30);

        walkRepository.save(createWalk(foundMember, savedTrailA, startTimeA, startTimeA.plusHours(1),
                LocalDate.of(2025, 7, 20), null, null));
        walkRepository.save(createWalk(foundMember, savedTrailB, startTimeB, startTimeB.plusMinutes(45),
                LocalDate.of(2025, 7, 21), null, null));

        // when
        List<Walk> foundWalks = walkRepository.findByMember(foundMember);

        // then
        assertThat(foundWalks).hasSize(2);
        assertThat(foundWalks)
                .extracting("trail.title", "date")
                .containsExactlyInAnyOrder(
                        tuple(savedTrailA.getTitle(), LocalDate.of(2025, 7, 20)),
                        tuple(savedTrailB.getTitle(), LocalDate.of(2025, 7, 21))
                );
    }

    @Test
    @DisplayName("산책 기록 삭제")
    void deleteWalk_success() {
        // given
        Member member = createMember("d@email.com", "회원임");
        Member savedMember = memberRepository.save(member);
        Member foundMember = memberRepository.findByEmail(savedMember.getEmail()).get();

        Trail savedTrail = trailRepository.save(createTrail(foundMember, "해운대구", "해운대구 근처 산책로 입니다.", 3.2));

        LocalDateTime startTime = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        Walk savedWalk = walkRepository.save(createWalk(foundMember, savedTrail, startTime, null, today, null, null));

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
        Member member = createMember("d@email.com", "회원임");
        Member savedMember = memberRepository.save(member);
        Member foundMember = memberRepository.findByEmail(savedMember.getEmail()).get();

        Trail trailA = trailRepository.save(createTrail(foundMember, "해운대구", "해운대구 산책로", 3.2));
        Trail trailB = trailRepository.save(createTrail(foundMember, "광안리", "광안리 산책로", 2.5));

        LocalDateTime startTimeA = LocalDateTime.of(2025, 7, 20, 10, 0);
        LocalDateTime startTimeB = LocalDateTime.of(2025, 7, 21, 15, 30);

        Walk walkA = walkRepository.save(createWalk(foundMember, trailA, startTimeA,
                startTimeA.plusHours(1), LocalDate.of(2025, 7, 20), null, null));
        Walk walkB = walkRepository.save(createWalk(foundMember, trailB, startTimeB,
                startTimeB.plusMinutes(45), LocalDate.of(2025, 7, 21), null, null));

        // when
        walkRepository.deleteById(walkA.getWalkId());

        // then
        List<Walk> walksAfterDelete = walkRepository.findByMember(foundMember);
        assertThat(walksAfterDelete).hasSize(1);
        assertThat(walksAfterDelete)
                .extracting("trail.title")
                .containsExactly(trailB.getTitle());
    }


}
