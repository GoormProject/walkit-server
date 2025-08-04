package life.walkit.server.trail.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.util.List;

public record TrailCreateRequest(

    @NotNull
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

    @NotNull
    Double length,

    @URL
    String routeImageUrl,

    @NotNull
    GeoPoint geoPoint,

    @NotNull
    List<GeoPoint> path,

    @NotNull
    Boolean isUploaded
) {
}
