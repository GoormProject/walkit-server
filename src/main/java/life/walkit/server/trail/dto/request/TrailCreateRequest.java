package life.walkit.server.trail.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.util.List;

public record TrailCreateRequest(

    @NotBlank
    Long walkId,

    @Size(
        min = 1,
        max = 100
    )
    String title,

    @Size(
        min = 1,
        max = 100
    )
    String description,

    @Size(
        min = 1,
        max = 100
    )
    String location,

    @NotBlank
    Double length,

    @URL
    String roteImageUrl,

    @NotBlank
    GeoPoint geoPoint,

    @NotBlank
    List<GeoPoint> path,

    @NotBlank
    Boolean isUploaded
) {
}
