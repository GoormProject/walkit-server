package life.walkit.server.trail.service;

import life.walkit.server.member.entity.Member;
import life.walkit.server.member.repository.MemberRepository;
import life.walkit.server.path.entity.Path;
import life.walkit.server.path.repository.PathRepository;
import life.walkit.server.trail.dto.request.GeoPoint;
import life.walkit.server.trail.dto.request.TrailCreateRequest;
import life.walkit.server.trail.dto.response.TrailCreateResponse;
import life.walkit.server.trail.entity.Trail;
import life.walkit.server.trail.repository.TrailRepository;
import life.walkit.server.walk.entity.Walk;
import life.walkit.server.walk.repository.WalkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static life.walkit.server.global.factory.GlobalTestFactory.createMember;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class TrailServiceTest {

    @Autowired
    private TrailService trailService;

    @Autowired
    private TrailRepository trailRepository;

    @Autowired
    private WalkRepository walkRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PathRepository pathRepository;

    private Member member;
    private Walk walk;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(createMember("test@walkit.life", "tester"));
        walk = walkRepository.save(
            Walk.builder()
                .member(member)
                .build()
        );
    }

    @Test
    @DisplayName("산책로 생성 성공")
    void createTrail_success() {
        // given
        GeoPoint startPoint = new GeoPoint(127.0, 37.5);
        List<GeoPoint> path = List.of(
            new GeoPoint(127.0, 37.5),
            new GeoPoint(127.1, 37.6)
        );

        TrailCreateRequest request = new TrailCreateRequest(
            walk.getWalkId(),
            "아름다운 산책로",
            "이곳은 테스트를 위한 산책로입니다.",
            "서울시 강남구",
            12.3,
            "http://example.com/route.jpg",
            startPoint,
            path,
            false // 요청값과 상관없이 서비스에서 true로 업데이트하는지 검증
        );

        // when
        TrailCreateResponse response = trailService.createTrail(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.walkId()).isEqualTo(walk.getWalkId());
        assertThat(response.trailId()).isNotNull();
        assertThat(response.isUploaded()).isTrue();

        // DB에 Trail이 잘 저장되었는지 검증
        Trail foundTrail = trailRepository.findById(response.trailId()).orElseThrow();
        assertThat(foundTrail.getTitle()).isEqualTo(request.title());
        assertThat(foundTrail.getDescription()).isEqualTo(request.description());
        assertThat(foundTrail.getDistance()).isEqualTo(request.length());
        assertThat(foundTrail.getLocation()).isEqualTo(request.location());
        assertThat(foundTrail.getPath()).isNotNull();
        assertThat(foundTrail.getPath().getPoint().getX()).isEqualTo(startPoint.longitude());
        assertThat(foundTrail.getPath().getPoint().getY()).isEqualTo(startPoint.latitude());

        // 원본 Walk 엔티티가 잘 업데이트되었는지 검증
        Walk updatedWalk = walkRepository.findById(walk.getWalkId()).orElseThrow();
        assertThat(updatedWalk.getTrail()).isNotNull();
        assertThat(updatedWalk.getTrail().getTrailId()).isEqualTo(foundTrail.getTrailId());
        assertThat(updatedWalk.getIsUploaded()).isTrue();
    }

    @Test
    @DisplayName("산책로 상세 조회 성공")
    void getTrailDetail_success() {
        // given
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Coordinate[] coordinates = new Coordinate[]{
            new Coordinate(127.0, 37.5),
            new Coordinate(127.1, 37.6)
        };
        Path path = pathRepository.save(Path.builder()
            .point(geometryFactory.createPoint(coordinates[0]))
            .path(geometryFactory.createLineString(coordinates))
            .build());

        Trail trail = trailRepository.save(Trail.builder()
            .member(member)
            .path(path)
            .title("테스트 산책로")
            .description("상세 조회 테스트용입니다.")
            .distance(5.5)
            .location("서울시 테스트구")
            .build());

        // when
        life.walkit.server.trail.dto.response.TrailDetailResponse response = trailService.getTrailDetail(trail.getTrailId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo(trail.getTitle());
        assertThat(response.description()).isEqualTo(trail.getDescription());
        assertThat(response.location()).isEqualTo(trail.getLocation());
        assertThat(response.length()).isEqualTo(trail.getDistance());
        assertThat(response.reviewCount()).isEqualTo(0); // 임시값 검증
        assertThat(response.rating()).isEqualTo(0.0); // 임시값 검증

        // 좌표 데이터 검증
        assertThat(response.startPoint()).containsExactly(coordinates[0].getX(), coordinates[0].getY());
        assertThat(response.path().get(0)).containsExactly(coordinates[0].getX(), coordinates[0].getY());
        assertThat(response.path().get(1)).containsExactly(coordinates[1].getX(), coordinates[1].getY());
    }
}
