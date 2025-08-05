package life.walkit.server.weather.controller;

import life.walkit.server.global.response.BaseResponse;
import life.walkit.server.weather.dto.ClothResponse;
import life.walkit.server.weather.dto.WeatherForecastResponseDto;
import life.walkit.server.weather.dto.enums.WeatherResponse;
import life.walkit.server.weather.entity.AdminArea;
import life.walkit.server.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/weather")
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<BaseResponse<WeatherForecastResponseDto>> getWeatherByCurrentLocation(
            @RequestParam double latitude,
            @RequestParam double longitude
    ) {
        AdminArea area = weatherService.findNearestAdminArea(latitude, longitude);
        WeatherForecastResponseDto dto = weatherService.fetchForecasts(area);
        return BaseResponse.toResponseEntity(WeatherResponse.GET_NEAR_WEATHER, dto);
    }

    @GetMapping("/clothing")
    public ResponseEntity<BaseResponse<ClothResponse>> getClothRecommendations(
            @RequestParam double latitude,
            @RequestParam double longitude
    ) {
        AdminArea area = weatherService.findNearestAdminArea(latitude, longitude);

        return BaseResponse.toResponseEntity(
                WeatherResponse.GET_CLOTH_RECOMMENDATION,
                weatherService.getClothRecommendations(area)
        );
    }
}
