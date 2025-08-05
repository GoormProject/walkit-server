package life.walkit.server.weather.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import life.walkit.server.global.response.BaseResponse;
import life.walkit.server.weather.dto.ClothResponse;
import life.walkit.server.weather.dto.WeatherForecastResponseDto;
import life.walkit.server.weather.dto.enums.WeatherResponse;
import life.walkit.server.weather.entity.AdminArea;
import life.walkit.server.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "날씨", description = "날씨 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/weather")
public class WeatherController {
    private final WeatherService weatherService;

    @Operation(summary = "주변 날씨 조회", description = "내 주변의 날씨 예보를 조회합니다.")
    @GetMapping
    public ResponseEntity<BaseResponse<WeatherForecastResponseDto>> getWeatherByCurrentLocation(
            @RequestParam double latitude,
            @RequestParam double longitude
    ) {
        AdminArea area = weatherService.findNearestAdminArea(latitude, longitude);
        WeatherForecastResponseDto dto = weatherService.fetchForecasts(area);
        return BaseResponse.toResponseEntity(WeatherResponse.GET_NEAR_WEATHER, dto);
    }

    @Operation(summary = "맞춤 옷차림 조회", description = "내 주변에 추천하는 옷차림을 조회합니다.")
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
