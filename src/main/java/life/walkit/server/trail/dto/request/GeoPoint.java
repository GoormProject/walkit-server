package life.walkit.server.trail.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import life.walkit.server.trail.error.enums.TrailErrorCode;
import life.walkit.server.trail.error.enums.TrailException;

import java.util.List;

public record GeoPoint(double longitude, double latitude) {

    @JsonCreator
    public GeoPoint(List<Double> coords) {
        this(coords.get(0), coords.get(1));

        if (coords.size() <= 3) {
            throw new TrailException(TrailErrorCode.TRAIL_START_POINT_INVALID);
        }

    }
}
