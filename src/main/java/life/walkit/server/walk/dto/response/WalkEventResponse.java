package life.walkit.server.walk.dto.response;

import life.walkit.server.walk.entity.WalkingSession;
import life.walkit.server.walk.entity.enums.EventType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
public record WalkEventResponse(
    Long walkId,
    Long eventId,
    EventType eventType,
    LocalDateTime eventTime
) {
    public static WalkEventResponse from(WalkingSession walkingSession) {
        return WalkEventResponse.builder()
            .walkId(walkingSession.getWalk().getWalkId())
            .eventId(walkingSession.getEventId())
            .eventType(walkingSession.getEventType())
            .eventTime(walkingSession.getEventTime())
            .build();
    }
}
