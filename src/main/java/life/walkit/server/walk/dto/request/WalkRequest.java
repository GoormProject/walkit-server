package life.walkit.server.walk.dto.request;

import java.util.List;

public record WalkRequest(
    Long walkId,
    String walkTitle,
    Integer totalTime,
    Double totalDistance,
    Double pace,
    List<List<Double>> path,
    List<Double> startPoint,
    Long eventId,
    String eventType,
    String routeUrl
) {

}
