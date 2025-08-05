package life.walkit.server.weather.model;

import life.walkit.server.weather.model.enums.Clouds;
import life.walkit.server.weather.model.enums.Night;
import life.walkit.server.weather.model.enums.Precipitation;
import life.walkit.server.weather.model.enums.Wind;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class ClothingRule {
    private Wind wind;              // 풍량
    private Clouds clouds;          // 구름
    private Precipitation precip;   // 강수
    private Night night;            // 주야
    private double min;             // 최저 온도(섭씨)
    private double max;             // 최고 온도(섭씨) : 범위 안이면 해당 옷차림 표시

    public boolean matches(Wind wind, Clouds clouds, Precipitation precip, Night night, double temp) {
        return Objects.equals(this.wind, wind) &&
                Objects.equals(this.clouds, clouds) &&
                Objects.equals(this.precip, precip) &&
                Objects.equals(this.night, night) &&
                (temp >= this.min && temp <= this.max);
    }
}
