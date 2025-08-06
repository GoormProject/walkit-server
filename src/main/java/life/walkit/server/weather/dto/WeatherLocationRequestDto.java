package life.walkit.server.weather.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WeatherLocationRequestDto {
    private double latitude;
    private double longitude;
}
