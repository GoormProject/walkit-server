package life.walkit.server.weather.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClothingRule {
    private String wind;            // 풍량
    private String clouds;          // 구름
    private String precip;          // 강수
    private Integer night;          // 주야
    private int min;                // 최저 온도(섭씨)
    private int max;                // 최고 온도(섭씨) : 범위 안이면 해당 옷차림 표시

    public boolean matches(String wind, String clouds, String precip, int night, int temp) {
        return (this.wind.isEmpty() || this.wind.equals(wind)) &&
                (this.clouds.isEmpty() || this.clouds.equals(clouds)) &&
                (this.precip.isEmpty() || this.precip.equals(precip)) &&
                (this.night.equals(night)) &&
                (temp >= this.min && temp <= this.max);
    }
}
