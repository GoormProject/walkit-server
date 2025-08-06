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

    public static Precipitation ofDescription(String description) {
        if (description == null || description.isEmpty()) {
            return NONE;
        }

        switch (description) {
            case "맑음":
                return NONE;
            case "비":
            case "비/눈":
            case "소나기":
                return RAIN;
            case "눈":
                return SNOW;
            default:
                return NONE;
        }
    }
}
