package life.walkit.server.weather.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import life.walkit.server.global.ratelimiter.ApiRateLimiter;
import life.walkit.server.weather.config.WeatherApiProperties;
import life.walkit.server.weather.dto.ClothResponse;
import life.walkit.server.weather.dto.WeatherDto;
import life.walkit.server.weather.dto.WeatherForecastResponseDto;
import life.walkit.server.weather.entity.AdminArea;
import life.walkit.server.weather.entity.Weather;
import life.walkit.server.weather.error.WeatherException;
import life.walkit.server.weather.error.enums.WeatherErrorCode;
import life.walkit.server.weather.model.enums.Clouds;
import life.walkit.server.weather.model.enums.Night;
import life.walkit.server.weather.model.enums.Precipitation;
import life.walkit.server.weather.model.enums.Wind;
import life.walkit.server.weather.repository.AdminAreaRepository;
import life.walkit.server.weather.repository.WeatherRepository;
import life.walkit.server.weather.utils.WeatherCodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple5;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {
    private final WeatherApiProperties properties;
    private final WebClient webClient;
    private final ApiRateLimiter rateLimiter;

    private final ClothingService clothingService;
    private final AdminAreaRepository adminAreaRepository;
    private final WeatherRepository weatherRepository;
    private final ObjectMapper objectMapper;

    public AdminArea findNearestAdminArea(double lat, double lon) {
        return adminAreaRepository.findNearestArea(lat, lon);
    }

    @Transactional
    public WeatherForecastResponseDto fetchForecasts(AdminArea area) {
        String nx = String.valueOf(area.getX());
        String ny = String.valueOf(area.getY());

        // 저장된 날씨가 오래되지 않았다면 그대로 사용 (락 걸고 조회)
        Weather savedWeather = weatherRepository.findByAdminAreaWithLock(area).orElse(null);
        if (savedWeather != null) {
            if (!savedWeather.isStale(LocalDateTime.now()))
                return buildResponseDto(area, savedWeather); // 1시간 이내 날씨는 사용
        }

        Mono<WeatherDto> current = parseWeatherData(fetchUltraSrtNcst(nx, ny), "obsrValue");
        Mono<WeatherDto> after3h = parseWeatherData(fetchUltraSrtFcst(nx, ny, 3), "fcstValue");
        Mono<WeatherDto> tomorrow = parseWeatherData(fetchVilageFcst(nx, ny, 1), "fcstValue");
        Mono<WeatherDto> dayAfterTomorrow = parseWeatherData(fetchVilageFcst(nx, ny, 2), "fcstValue");
        Mono<WeatherDto> threeDaysLater = parseWeatherData(fetchVilageFcst(nx, ny, 3), "fcstValue");

        // 비동기 요청 → block() 으로 동기 전환
        Tuple5<WeatherDto, WeatherDto, WeatherDto, WeatherDto, WeatherDto> tuple =
                Mono.zip(current, after3h, tomorrow, dayAfterTomorrow, threeDaysLater).block();

        // 오래된 날씨 삭제
        if (savedWeather != null) {
            weatherRepository.delete(savedWeather);
            weatherRepository.flush(); // 즉시 delete 실행
        }

        // 새로운 날씨 저장
        Weather weather = Weather.builder()
                .adminArea(area)
                .current(tuple.getT1().toMap())
                .forecast(tuple.getT2().toMap())
                .tomorrow(tuple.getT3().toMap())
                .dayAfterTomorrow(tuple.getT4().toMap())
                .threeDaysLater(tuple.getT5().toMap())
                .temperature(tuple.getT1().getTemperature())
                .humidity(tuple.getT1().getHumidity())
                .build();
        weatherRepository.save(weather);

        return buildResponseDto(area, weather);
    }

    @Transactional
    public ClothResponse getClothRecommendations(AdminArea area) {

        WeatherDto weather = weatherRepository.findByAdminArea(area)
                .filter(value -> !value.isStale(LocalDateTime.now())) // 오래된 날씨는 무시
                .map(value -> WeatherDto.of(value.getForecast()))
                .orElse(fetchForecasts(area).getAfter3hours()); // 유효한 날씨가 없으면 새로 가져오기

        List<String> recommendations = clothingService.getClothRecommendations(
                Wind.of(weather.getWindSpeed()),
                Clouds.of(weather.getClouds()),
                Precipitation.ofDescription(weather.getWeather()),
                Night.of(LocalTime.now()),
                weather.getTemperature()
        );

        return ClothResponse.builder()
                .recommendations(recommendations)
                .build();
    }

    private WeatherForecastResponseDto buildResponseDto(AdminArea area, Weather weather) {
        String locationName = area.getSido() + " " + area.getSigungu() + " " + area.getEupmyeondong();
        locationName = locationName.trim();

        return WeatherForecastResponseDto.builder()
                .adminAreaName(locationName)
                .current(WeatherDto.of(weather.getCurrent()))
                .after3hours(WeatherDto.of(weather.getForecast()))
                .tomorrow(WeatherDto.of(weather.getTomorrow()))
                .dayAfterTomorrow(WeatherDto.of(weather.getDayAfterTomorrow()))
                .threeDaysLater(WeatherDto.of(weather.getThreeDaysLater()))
                .build();
    }

    // 초단기실황 API 호출 (현재날씨)
    private Mono<JsonNode> fetchUltraSrtNcst(String nx, String ny) {
        LocalDateTime now = LocalDateTime.now().minusHours(1);
        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = now.format(DateTimeFormatter.ofPattern("HH00"));

        StringBuilder urlBuilder = new StringBuilder(properties.getUltraSrtNcstUrl());
        urlBuilder.append("?serviceKey=").append(properties.getServiceKey());
        urlBuilder.append("&dataType=JSON");
        urlBuilder.append("&numOfRows=1000");
        urlBuilder.append("&pageNo=1");
        urlBuilder.append("&base_date=").append(baseDate);
        urlBuilder.append("&base_time=").append(baseTime);
        urlBuilder.append("&nx=").append(nx);
        urlBuilder.append("&ny=").append(ny);

        return fetchJsonItems(urlBuilder.toString());
    }

    // 초단기예보 API 호출 (3시간 후 날씨)
    private Mono<JsonNode> fetchUltraSrtFcst(String nx, String ny, int hourOffset) {
        LocalDateTime base = LocalDateTime.now().minusMinutes(30);
        String baseDate = base.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = base.format(DateTimeFormatter.ofPattern("HH30"));

        String targetDate = LocalDateTime.now().plusHours(hourOffset).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String targetTime = LocalDateTime.now().plusHours(hourOffset).format(DateTimeFormatter.ofPattern("HH00"));

        StringBuilder urlBuilder = new StringBuilder(properties.getUltraSrtFcstUrl());
        urlBuilder.append("?serviceKey=").append(properties.getServiceKey());
        urlBuilder.append("&dataType=JSON");
        urlBuilder.append("&numOfRows=1000");
        urlBuilder.append("&pageNo=1");
        urlBuilder.append("&base_date=").append(baseDate);
        urlBuilder.append("&base_time=").append(baseTime);
        urlBuilder.append("&nx=").append(nx);
        urlBuilder.append("&ny=").append(ny);

        Mono<JsonNode> itemsMono = fetchJsonItems(urlBuilder.toString());
        return filterByDateTime(itemsMono, targetDate, targetTime);
    }

    // 단기예보 API 호출 (1일 후, 2일 후 날씨)
    private Mono<JsonNode> fetchVilageFcst(String nx, String ny, int dayOffset) {
        String baseDate = LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = "0200";
        String targetDate = LocalDateTime.now().plusDays(dayOffset).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        StringBuilder urlBuilder = new StringBuilder(properties.getVilageFcstUrl());
        urlBuilder.append("?serviceKey=").append(properties.getServiceKey());
        urlBuilder.append("&dataType=JSON");
        urlBuilder.append("&numOfRows=1000");
        urlBuilder.append("&pageNo=1");
        urlBuilder.append("&base_date=").append(baseDate);
        urlBuilder.append("&base_time=").append(baseTime);
        urlBuilder.append("&nx=").append(nx);
        urlBuilder.append("&ny=").append(ny);

        return filterByDateTime(fetchJsonItems(urlBuilder.toString()), targetDate, "1200");
    }

    // filterByDateTime : JSON 배열을 날짜(fcstDate)와 시간(fcstTime) 기준으로 필터링
    private Mono<JsonNode> filterByDateTime(Mono<JsonNode> itemsMono, String date, String time) {
        return itemsMono.map(items -> {
            ArrayNode filtered = objectMapper.createArrayNode();
            for (JsonNode item : items) {
                if (item.has("fcstDate") && item.has("fcstTime") &&
                        item.get("fcstDate").asText().equals(date) &&
                        item.get("fcstTime").asText().equals(time)) {
                    filtered.add(item);
                }
            }
            return filtered;
        });
    }

    // 날씨 API 응답에서 실제 필요한 카테고리값을 추출해 WeatherDto를 생성
    // 현재기온, 강수형태, 습도, 풍속, 강수확률
    private Mono<WeatherDto> parseWeatherData(Mono<JsonNode> itemsMono, String valueField) {
        return itemsMono.map(items -> {
            Map<String, String> map = new HashMap<>();
            for (JsonNode item : items) {
                map.put(item.get("category").asText(), item.get(valueField).asText());
            }

            return WeatherDto.builder()
                    .temperature(Double.parseDouble(map.getOrDefault("T1H", map.getOrDefault("TMP", "0"))))
                    .weather(WeatherCodeMapper.getWeatherDescription(map.getOrDefault("PTY", "0")))
                    .humidity(Integer.parseInt(map.getOrDefault("REH", "0")))
                    .windSpeed(Double.parseDouble(map.getOrDefault("WSD", "0")))
                    .clouds(Integer.parseInt(map.getOrDefault("SKY", "-1")))
                    .build();
        });
    }

    // HTTP GET 요청을 보내고, JSON 응답에서 .response.body.items.item 노드를 파싱
    public Mono<JsonNode> fetchJsonItems(String urlString) {
        return Mono.defer(() -> {
                    if (!rateLimiter.tryConsume()) {
                        return Mono.error(new WeatherException(WeatherErrorCode.WEATHER_RATE_LIMIT_EXCEEDED));
                    }

                    return webClient.get()
                            .uri(URI.create(urlString))
                            .retrieve()
                            .bodyToMono(String.class)
                            .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))  // 최대 3회 재시도
                            .flatMap(response -> {
                                try {
                                    JsonNode root = objectMapper.readTree(response);
                                    JsonNode items = root.path("response").path("body").path("items").path("item");
                                    return Mono.just(items);
                                } catch (Exception e) {
                                    return Mono.error(new WeatherException(WeatherErrorCode.WEATHER_JSON_PARSE_FAILED, e));
                                }
                            });
                })
                .retryWhen(reactor.util.retry.Retry.backoff(10, Duration.ofMillis(100))
                        .filter(ex -> ex instanceof WeatherException &&
                                ((WeatherException) ex).getErrorCode() == WeatherErrorCode.WEATHER_RATE_LIMIT_EXCEEDED)
                );
    }
}
