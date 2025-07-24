package life.walkit.server.trail.repository;

import life.walkit.server.member.entity.Member;
import life.walkit.server.member.repository.MemberRepository;
import life.walkit.server.trail.entity.Trail;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static life.walkit.server.global.factory.GlobalTestFactory.createMember;
import static life.walkit.server.global.factory.GlobalTestFactory.createTrail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@ActiveProfiles("test")
public class TrailRepositoryTest {

    @Autowired
    private TrailRepository trailRepository;

    @Autowired
    private MemberRepository memberRepository;

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

    @AfterEach
    void tearDown() {
        trailRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("산책로 저장 성공")
    void save_success() {
        // given
        Member member = createMember("d@email.com", "회원임");
        Member savedMember = memberRepository.save(member);
        Member foundMember = memberRepository.findByEmail(savedMember.getEmail()).get();
        
        // when
        Trail savedTrail = trailRepository.save(createTrail(foundMember, "해운대구", "해운대구 근처 산책로 입니다.", 3.2));

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
        Member member = createMember("d@email.com", "회원임");
        Member savedMember = memberRepository.save(member);
        Member foundMember = memberRepository.findByEmail(savedMember.getEmail()).get();
        Trail savedTrailA = trailRepository.save(createTrail(foundMember, "해운대구", "해운대구 근처 산책로 입니다.", 3.2));
        Trail savedTrailB = trailRepository.save(createTrail(foundMember, "동성로", "동성로 근처 산책로 입니다.", 2.2));

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
