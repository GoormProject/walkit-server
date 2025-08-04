package life.walkit.server.weather.controller;

import life.walkit.server.weather.dto.WeatherForecastResponseDto;
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

    @GetMapping("/location")
    public ResponseEntity<WeatherForecastResponseDto> getWeatherByCurrentLocation(
            @RequestParam double latitude,
            @RequestParam double longitude
    ) throws Exception {
        AdminArea area = weatherService.findNearestAdminArea(latitude, longitude);
        WeatherForecastResponseDto dto = weatherService.fetchForecasts(area).block();
        return ResponseEntity.ok(dto);
    }




}
