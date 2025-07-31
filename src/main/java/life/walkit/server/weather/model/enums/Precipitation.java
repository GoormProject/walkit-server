package life.walkit.server.weather.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

// 강수
public enum Precipitation {
    NONE, RAIN, SNOW;

    @JsonCreator
    public static Precipitation of(String value) {
        try {
            return Precipitation.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }
}
