package life.walkit.server.trail.repository;

import life.walkit.server.member.entity.Member;
import life.walkit.server.member.repository.MemberRepository;
import life.walkit.server.path.entity.Path;
import life.walkit.server.path.repository.PathRepository;
import life.walkit.server.trail.entity.Trail;
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
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class TrailRepositoryTest {

    @Autowired
    private TrailRepository trailRepository;

    @Autowired
    private MemberRepository memberRepository;
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
        memberRepository.save(createMember("c@email.com", "회원C"));
        Member memberC = memberRepository.findByEmail("c@email.com").get();
        Path savePath = pathRepository.save(createPath(lineString));

        trailRepository.save(createTrail(memberC, "도원동", "도원동 근처 산책로 입니다.", 1.2, savePath));
    }

    @Test
    @DisplayName("산책로 저장 성공")
    void save_success() {
        // given
        Double[][] lineString = new Double[][]{
                {126.986, 37.541},
                {126.987, 37.542},
                {126.988, 37.543},
                {126.989, 37.544}
        };
        Member member = createMember("d@email.com", "회원임");
        Member savedMember = memberRepository.save(member);
        Member foundMember = memberRepository.findByEmail(savedMember.getEmail()).get();
        Path path = pathRepository.save(createPath(lineString));

        // when
        Trail savedTrail = trailRepository.save(
            createTrail(
                foundMember,
                "해운대구",
                "해운대구 근처 산책로 입니다.",
                3.2,
                path
            )
        );

        // then
        List<Trail> foundTrail = trailRepository.findByMember(savedTrail.getMember());
        assertThat(foundTrail)
                .extracting("title")
                .containsExactly("해운대구");

    }

    @Test
    @DisplayName("산책로명으로 조회")
    void findByTitleContaining_success() {
        // when & then
        List<Trail> foundTrail = trailRepository.findByTitle("도원동");
        assertThat(foundTrail)
                .extracting("title", "distance")
                .containsExactly(tuple("도원동", 1.2));
    }

    @Test
    @DisplayName("산책로 목록 조회")
    void findTrailList_success() {
        // given
        Double[][] lineStringA = new Double[][]{
                {126.986, 37.541},
                {126.987, 37.542},
                {126.988, 37.543},
                {126.989, 37.544}
        };
        Double[][] lineStringB = new Double[][]{
                {126.986, 37.541},
                {126.987, 37.542},
                {126.988, 37.543},
                {126.989, 37.544}
        };

        Path pathA = pathRepository.save(createPath(lineStringA));
        Path pathB = pathRepository.save(createPath(lineStringB));
        Path foundPathA = pathRepository.findById(pathA.getPathId()).get();
        Path foundPathB = pathRepository.findById(pathB.getPathId()).get();

        Member member = createMember("d@email.com", "회원임");
        Member savedMember = memberRepository.save(member);
        Member foundMember = memberRepository.findByEmail(savedMember.getEmail()).get();
        Trail savedTrailA = trailRepository.save(
            createTrail(
                foundMember,
                "해운대구",
                "해운대구 근처 산책로 입니다.",
                3.2,
                foundPathA
            )
        );
        Trail savedTrailB = trailRepository.save(
            createTrail(
                foundMember,
                "동성로",
                "동성로 근처 산책로 입니다.",
                2.2,
                foundPathB
            )
        );

        // when
        List<Trail> trailList = trailRepository.findByMember(foundMember);

        // then
        assertThat(trailList)
                .hasSize(2)
                .extracting("title", "distance")
                .containsExactly(
                        tuple(savedTrailA.getTitle(), savedTrailA.getDistance()),
                        tuple(savedTrailB.getTitle(), savedTrailB.getDistance())
                );
    }

}
