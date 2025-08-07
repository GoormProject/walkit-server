package life.walkit.server.trail.dto.response;

import life.walkit.server.trail.entity.Trail;
import life.walkit.server.trailwalkimage.entity.TrailWalkImage;

import java.util.Optional;

public record TrailListResponse(
    Long trailId,
    String title,
    String location,
    Double length,
    String routeImageUrl,
    Long reviewCount,
    Double rating
) {
    public static TrailListResponse from(Trail trail, Optional<TrailWalkImage> trailWalkImageOpt, long reviewCount, double rating) {
        String imageUrl = trailWalkImageOpt.map(TrailWalkImage::getRouteImage).orElse(null);

        return new TrailListResponse(
                trail.getTrailId(),
                trail.getTitle(),
                trail.getLocation(),
                trail.getDistance(),
                imageUrl,
                reviewCount, // TODO: 리뷰 기능 구현 후 실제 값으로 변경
                rating       // TODO: 리뷰 기능 구현 후 실제 값으로 변경
        );
    }
}