package life.walkit.server.trail.dto.response;

import life.walkit.server.trail.entity.Trail;
import org.locationtech.jts.geom.Coordinate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record TrailDetailResponse(
    String title,
    String description,
    String location,
    Double length,
    String routeImageUrl,
    Integer reviewCount,
    Double rating,
    List<Double> startPoint,
    List<List<Double>> path
) {
    public static TrailDetailResponse from(Trail trail, String routeImageUrl, int reviewCount, double rating) {
        Coordinate startCoordinate = trail.getPath().getPoint().getCoordinate();
        List<Double> startPoint = List.of(startCoordinate.getX(), startCoordinate.getY());

        List<List<Double>> pathCoordinates = Arrays.stream(trail.getPath().getPath().getCoordinates())
                .map(c -> List.of(c.getX(), c.getY()))
                .collect(Collectors.toList());

        return new TrailDetailResponse(
                trail.getTitle(),
                trail.getDescription(),
                trail.getLocation(),
                trail.getDistance(), // Trail 엔티티의 distance 필드 사용
                routeImageUrl,
                reviewCount, // TODO: 리뷰 기능 구현 후 실제 값으로 변경
                rating,      // TODO: 리뷰 기능 구현 후 실제 값으로 변경
                startPoint,
                pathCoordinates
        );
    }
}
