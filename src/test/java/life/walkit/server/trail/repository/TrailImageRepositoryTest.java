package life.walkit.server.trail.repository;


import life.walkit.server.member.entity.Member;
import life.walkit.server.member.repository.MemberRepository;
import life.walkit.server.path.entity.Path;
import life.walkit.server.path.repository.PathRepository;
import life.walkit.server.trail.entity.Trail;
import life.walkit.server.trail.entity.TrailImage;
import life.walkit.server.walk.entity.Walk;
import life.walkit.server.walk.repository.WalkRepository;
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

@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class TrailImageRepositoryTest {

    @Autowired
    TrailImageRepository trailImageRepository;

    @Autowired
    TrailRepository trailRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    WalkRepository walkRepository;

    @Autowired
    PathRepository pathRepository;

    Trail foundTrail;

    @BeforeEach
    void setUp() {
        Double[][] lineString = new Double[][]{
            {126.986, 37.541},
            {126.987, 37.542},
            {126.988, 37.543},
            {126.989, 37.544}
        };

        memberRepository.save(createMember("a@email.com", "회원A"));
        Path savePath = pathRepository.save(createPath(lineString)); // Walk에 저장되야하는 Path
        Member foundMember = memberRepository.findByEmail("a@email.com").get();

        walkRepository.save(
            createWalk(
                    foundMember,
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    LocalDate.now(),
                    savePath,
                    null,
                    null
            )
        );

        // 복사한 Path
        Path trailPath = pathRepository.save(new Path(savePath.getPath()));

        trailRepository.save(
            createTrail(
                foundMember,
                "해운대구",
                "해운대구 근처 산책로 입니다.",
                3.2,
                trailPath
            )
        );

        foundTrail = trailRepository.findByMember(foundMember).get(0);


    }

    @Test
    @DisplayName("이미지 저장 성공")
    void saveImage_success() {
        // given
        String imageUrl = "https//aws.s3.com";
        trailImageRepository.save(new TrailImage(foundTrail, imageUrl));

        // when
        List<TrailImage> foundTrailImage = trailImageRepository.findByTrail(foundTrail);

        // then
        assertThat(foundTrailImage)
                .extracting("routeImage")
                .containsExactly(imageUrl);

    }

}
