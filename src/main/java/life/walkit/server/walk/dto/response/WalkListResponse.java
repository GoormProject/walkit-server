package life.walkit.server.walk.dto.response;

public record WalkListResponse(
    Long walkId,
    Long trailId,
    Long eventId,
    String eventTime,
    Long trailImageId,
    String routeImageUrl,
    Double totalDistance,
    String totalTime,
    String pace,
    String title,
    Boolean isUploaded
) {
}
