package life.walkit.server.weather.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeatherForecastResponseDto {
    private String adminAreaName;
    private WeatherDto current;
    private WeatherDto after3hours;
    private WeatherDto tomorrow;
    private WeatherDto dayAfterTomorrow;
    private WeatherDto threeDaysLater;
}
