package life.walkit.server.weather.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Wind {
    CALM, MODERATE, STRONG;

    @JsonCreator
    public static Wind of(String value) {
        try {
            return Wind.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CALM;
        }
    }
}
