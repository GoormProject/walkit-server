package life.walkit.server.trail.service;

import life.walkit.server.global.util.GeoUtils;
import life.walkit.server.member.repository.MemberRepository;
import life.walkit.server.path.entity.Path;
import life.walkit.server.path.repository.PathRepository;
import life.walkit.server.trail.dto.request.GeoPoint;
import life.walkit.server.trail.dto.request.TrailCreateRequest;
import life.walkit.server.trail.dto.response.TrailCreateResponse;
import life.walkit.server.trail.entity.Trail;
import life.walkit.server.trail.repository.TrailRepository;
import life.walkit.server.walk.entity.Walk;
import life.walkit.server.walk.error.enums.WalkErrorCode;
import life.walkit.server.walk.error.enums.WalkException;
import life.walkit.server.walk.repository.WalkRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrailService {
    private final TrailRepository trailRepository;
    private final WalkRepository walkRepository;
    private final MemberRepository memberRepository;

    private final PathRepository pathRepository;

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
            return new GeometryFactory(new PrecisionModel(), 4326).createLineString();
        }

        Coordinate[] coordinates = pathPoints.stream()
            .map(geoPoint -> new Coordinate(geoPoint.longitude(), geoPoint.latitude()))
            .toArray(Coordinate[]::new);

        return new GeometryFactory(new PrecisionModel(), 4326).createLineString(coordinates);
    }

    private Point convertStartPointToGeoPoint(GeoPoint startPoint) {
        double longitude = startPoint.longitude();
        double latitude = startPoint.latitude();

        return GeoUtils.toPoint(longitude, latitude);
    }
}
