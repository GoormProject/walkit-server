package life.walkit.server.trailwalkImage.repository;


import life.walkit.server.member.entity.Member;
import life.walkit.server.member.repository.MemberRepository;
import life.walkit.server.path.entity.Path;
import life.walkit.server.path.repository.PathRepository;
import life.walkit.server.trail.entity.Trail;
import life.walkit.server.trailwalkimage.repository.TrailWalkImageRepository;
import life.walkit.server.trail.repository.TrailRepository;
import life.walkit.server.trailwalkimage.entity.TrailWalkImage;
import life.walkit.server.walk.entity.Walk;
import life.walkit.server.walk.repository.WalkRepository;
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

import static life.walkit.server.global.factory.GlobalTestFactory.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class TrailImageRepositoryTest {

    @Autowired
    TrailWalkImageRepository trailWalkImageRepository;

    @Autowired
    TrailRepository trailRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    WalkRepository walkRepository;

    @Autowired
    PathRepository pathRepository;

    Trail foundTrail;
    Member foundMember;

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
        foundMember = memberRepository.findByEmail("a@email.com").get();

        LocalTime startTime = LocalTime.of(0, 0, 0); // startTIme
        LocalTime endTime = LocalTime.of(12, 23, 56); // endTime

        Duration totalTime = Duration.between(startTime, endTime); // 12 hour
        Double totalDistance = 2.0; // 총 거리
        Double pace = 1.2; // 평균 속도 1.2km

        walkRepository.save(
            createWalk(
                foundMember,
                null,
                savePath,
                "테스트 타이틀",
                totalDistance,
                totalTime,
                pace,
                false
            )
        );

        // 복사한 Path
        Path trailPath = pathRepository.save(
            Path
                .builder()
                .path(savePath.getPath())
                .point(savePath.getPoint())
                .build()
        );

        trailRepository.save(
            createTrail(
                foundMember,
                "해운대구",
                "해운대구 근처 산책로 입니다.",
                3.2,
                "부산 해운대구",
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
        Walk walk = walkRepository.findByMember(foundMember).get(0);

        trailWalkImageRepository.save(
            createTrailWalkImage(imageUrl, null, walk)
        );

        // when
        List<TrailWalkImage> foundTrailImage = trailWalkImageRepository.findByWalk(walk);

        // then
        assertThat(foundTrailImage)
            .extracting("routeImage")
            .containsExactly(imageUrl);

    }

}
