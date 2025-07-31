package life.walkit.server.weather.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Night {
    DAYTIME, NIGHTTIME;

    @JsonCreator
    public static Night of(String value) {
        if ("1".equals(value)) return NIGHTTIME;
        else return DAYTIME;
    }
}
