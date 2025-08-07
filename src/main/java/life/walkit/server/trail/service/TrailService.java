package life.walkit.server.trail.service;

import life.walkit.server.global.util.GeoUtils;
import life.walkit.server.path.entity.Path;
import life.walkit.server.path.repository.PathRepository;
import life.walkit.server.review.dto.ReviewStats;
import life.walkit.server.review.repository.ReviewRepository;
import life.walkit.server.trail.dto.request.GeoPoint;
import life.walkit.server.trail.dto.request.TrailCreateRequest;
import life.walkit.server.trail.dto.response.TrailCreateResponse;
import life.walkit.server.trail.dto.response.TrailDetailResponse;
import life.walkit.server.trail.dto.response.TrailListResponse;
import life.walkit.server.trail.entity.Trail;
import life.walkit.server.trail.error.enums.TrailErrorCode;
import life.walkit.server.trail.error.enums.TrailException;
import life.walkit.server.trail.repository.TrailRepository;
import life.walkit.server.trailwalkimage.repository.TrailWalkImageRepository;
import life.walkit.server.walk.entity.Walk;
import life.walkit.server.walk.error.enums.WalkErrorCode;
import life.walkit.server.walk.error.enums.WalkException;
import life.walkit.server.walk.repository.WalkRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrailService {
    private final TrailRepository trailRepository;
    private final WalkRepository walkRepository;
    private final PathRepository pathRepository;
    private final ReviewRepository reviewRepository;

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    @Transactional
    public TrailCreateResponse createTrail(TrailCreateRequest request) {
        Walk foundWalk = walkRepository.findById(request.walkId())
            .orElseThrow(() -> new WalkException(WalkErrorCode.WALK_NOT_FOUND));

        LineString lineString = createLineString(request.path());
        Point point = convertStartPointToGeoPoint(request.geoPoint());

        Path newPath = pathRepository.save(Path.builder()
            .path(lineString)
            .point(point)
            .build());

        Trail newTrail = Trail.builder()
            .description(request.description())
            .distance(request.length())
            .location(request.location())
            .member(foundWalk.getMember())
            .path(newPath)
            .title(request.title())
            .build();

        trailRepository.save(newTrail);
        foundWalk.updateTrail(newTrail);
        foundWalk.updateIsUploaded(true);

        return new TrailCreateResponse(foundWalk.getWalkId(), newTrail.getTrailId(), newTrail.getCreatedAt(), true);
    }

    private LineString createLineString(List<GeoPoint> pathPoints) {
        if (pathPoints == null || pathPoints.isEmpty()) {
            return GEOMETRY_FACTORY.createLineString();
        }

        Coordinate[] coordinates = pathPoints.stream()
            .map(geoPoint -> new Coordinate(geoPoint.longitude(), geoPoint.latitude()))
            .toArray(Coordinate[]::new);

        return GEOMETRY_FACTORY.createLineString(coordinates);
    }

    private Point convertStartPointToGeoPoint(GeoPoint startPoint) {
        double longitude = startPoint.longitude();
        double latitude = startPoint.latitude();

        return GeoUtils.toPoint(longitude, latitude);
    }

    @Transactional(readOnly = true)
    public TrailDetailResponse getTrailDetail(Long trailId) {
        Trail trail = trailRepository.findById(trailId)
            .orElseThrow(() -> new TrailException(TrailErrorCode.TRAIL_SELECT_FAILED));

        // TODO: 이미지 S3연결 후 리펙토링 필요
        String imageUrl = "example.com";

        ReviewStats review = reviewRepository.findReviewStatsByTrailId(trailId)
                .orElse(new ReviewStats(trailId, 0.0, 0L));

        return TrailDetailResponse.from(trail, imageUrl, review.count(), review.rating());
    }

    @Transactional(readOnly = true)
    public Page<TrailListResponse> getTrailList(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Trail> trailsPage = trailRepository.findAll(pageable);

        return trailsPage.map(trail -> {
            // TODO: 이미지 S3연결 후 리펙토링 필요
            String imageUrl = "example.com"; // 임시 이미지 URL

            ReviewStats review = reviewRepository.findReviewStatsByTrailId(trail.getTrailId())
                    .orElse(new ReviewStats(trail.getTrailId(), 0.0, 0L));

            return TrailListResponse.from(trail, Optional.empty(), review.count(), review.rating()); // Optional.empty()는 임시
        });
    }
}
