package life.walkit.server.weather.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeatherDto {
    private double temperature;
    private String weather;
    private int humidity;
    private double windSpeed;
}
