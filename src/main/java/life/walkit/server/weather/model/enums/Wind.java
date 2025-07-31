package life.walkit.server.weather.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Wind {
    CALM, MODERATE, STRONG;

    @JsonCreator
    public static Wind of(String value) {
        if (value == null || value.isBlank()) return CALM;
        return Wind.valueOf(value.toUpperCase());
    }
}
