package life.walkit.server.trail.dto.response;

import java.time.LocalDateTime;

public record TrailCreateResponse(
    Long walkId,
    Long trailId,
    LocalDateTime createdAt,
    Boolean isUploaded
) {
}
