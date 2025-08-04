package life.walkit.server.trail.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import life.walkit.server.trail.error.enums.TrailErrorCode;
import life.walkit.server.trail.error.enums.TrailException;

import java.util.List;

public record GeoPoint(double longitude, double latitude) {

    @JsonCreator
    public GeoPoint(List<Double> coords) {
        this(validateAndGetLongitude(coords), validateAndGetLatitude(coords));
    }

    private static double validateAndGetLongitude(List<Double> coords) {
        if (coords == null || coords.size() < 2) {
            throw new TrailException(TrailErrorCode.TRAIL_START_POINT_INVALID);
        }
        return coords.get(0);
    }

    private static double validateAndGetLatitude(List<Double> coords) {
        // 추후 추가할 수 있음
        return coords.get(1); // 이미 위에서 검증됨
    }
}
