package life.walkit.server.weather.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import life.walkit.server.weather.config.WeatherApiProperties;
import life.walkit.server.weather.dto.WeatherDto;
import life.walkit.server.weather.dto.WeatherForecastResponseDto;
import life.walkit.server.weather.entity.AdminArea;
import life.walkit.server.weather.entity.Weather;
import life.walkit.server.weather.repository.AdminAreaRepository;
import life.walkit.server.weather.repository.WeatherRepository;
import life.walkit.server.weather.utils.WeatherCodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple5;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {
    private final WeatherApiProperties properties;

    private final WebClient webClient = WebClient.builder().build();
    private final AdminAreaRepository adminAreaRepository;
    private final ObjectMapper objectMapper;
    private final WeatherRepository weatherRepository;

    public AdminArea findNearestAdminArea(double lat, double lon) {
        return adminAreaRepository.findNearestArea(lat, lon);
    }

    public WeatherForecastResponseDto fetchForecasts(AdminArea area) throws Exception {
        String nx = String.valueOf(area.getX());
        String ny = String.valueOf(area.getY());

        // 저장된 날씨가 오래되지 않았다면 그대로 사용
        Weather savedWeather = weatherRepository.findByAdminArea(area).orElse(null);
        if (savedWeather != null) {
            if (savedWeather.isStale(LocalDateTime.now()))
                weatherRepository.delete(savedWeather); // 오래된 날씨는 삭제
            else
                return buildResponseDto(area, savedWeather); // 1시간 이내 날씨는 사용
        }


        Mono<WeatherDto> current = Mono.delay(Duration.ofMillis(0))
                .then(parseWeatherData(fetchUltraSrtNcst(nx, ny), "obsrValue"));
        Mono<WeatherDto> after3h = Mono.delay(Duration.ofMillis(10))
                .then(parseWeatherData(fetchUltraSrtFcst(nx, ny, 3), "fcstValue"));
        Mono<WeatherDto> tomorrow = Mono.delay(Duration.ofMillis(20))
                .then(parseWeatherData(fetchVilageFcst(nx, ny, 1), "fcstValue"));
        Mono<WeatherDto> dayAfterTomorrow = Mono.delay(Duration.ofMillis(30))
                .then(parseWeatherData(fetchVilageFcst(nx, ny, 2), "fcstValue"));
        Mono<WeatherDto> threeDaysLater = Mono.delay(Duration.ofMillis(40))
                .then(parseWeatherData(fetchVilageFcst(nx, ny, 3), "fcstValue"));

        // 비동기 요청 → block() 으로 동기 전환
        Tuple5<WeatherDto, WeatherDto, WeatherDto, WeatherDto, WeatherDto> tuple =
                Mono.zip(current, after3h, tomorrow, dayAfterTomorrow, threeDaysLater).block();

        // 날씨 저장
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

    private WeatherForecastResponseDto buildResponseDto(AdminArea area, Weather weather) {
        String locationName = area.getSido() + " " + area.getSigungu() + " " + area.getEupmyeondong();

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
    private Mono<JsonNode> fetchUltraSrtNcst(String nx, String ny) throws Exception {
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
    private Mono<JsonNode> fetchUltraSrtFcst(String nx, String ny, int hourOffset) throws Exception {
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
    private Mono<JsonNode> fetchVilageFcst(String nx, String ny, int dayOffset) throws Exception {
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
        return webClient.get()
                .uri(URI.create(urlString))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        JsonNode root = objectMapper.readTree(response);
                        JsonNode items = root.path("response").path("body").path("items").path("item");
                        return Mono.just(items);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("JSON 파싱 실패", e));
                    }
                });
    }
}
