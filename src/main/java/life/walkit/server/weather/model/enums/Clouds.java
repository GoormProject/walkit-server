package life.walkit.server.weather.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Clouds {
    CLEAR, PARTLY, OVERCAST;

    @JsonCreator
    public static Clouds of(String value) {
        try {
            return Clouds.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CLEAR;
        }
    }
}
