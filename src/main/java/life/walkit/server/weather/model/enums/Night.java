package life.walkit.server.weather.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.LocalTime;

public enum Night {
    DAYTIME, NIGHTTIME;

    @JsonCreator
    public static Night of(String value) {
        if ("1".equals(value)) return NIGHTTIME;
        else return DAYTIME;
    }

    public static Night of(LocalTime time) {
        LocalTime dayStart = LocalTime.of(6, 0);   // 06:00
        LocalTime dayEnd = LocalTime.of(18, 0);    // 18:00

        if (time.isBefore(dayStart) || time.isAfter(dayEnd)) {
            return NIGHTTIME;
        } else {
            return DAYTIME;
        }
    }
}
