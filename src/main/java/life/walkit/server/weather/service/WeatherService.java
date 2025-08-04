package life.walkit.server.weather.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import life.walkit.server.weather.dto.WeatherDto;
import life.walkit.server.weather.dto.WeatherForecastResponseDto;
import life.walkit.server.weather.entity.AdminArea;
import life.walkit.server.weather.repository.AdminAreaRepository;
import life.walkit.server.weather.utils.WeatherCodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {
    private static final String SERVICE_KEY = "Fg05Febh4v3dwcku1TPJILYtnkHHWqRzTatz%2F6EuAwp15c%2BKaQiDduo2nqMDw6Qballm5WN5TOV7AsGslFkdmw%3D%3D";
    private static final String ULTRA_SRT_NCST_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";
    private static final String ULTRA_SRT_FCST_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst";
    private static final String VILAGE_FCST_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";

    private final AdminAreaRepository adminAreaRepository;
    private final ObjectMapper objectMapper;

    public AdminArea findNearestAdminArea(double lat, double lon) {
        return adminAreaRepository.findNearestArea(lat, lon);
    }

    public WeatherForecastResponseDto fetchForecasts(AdminArea area) throws Exception {
        String nx = String.valueOf(area.getX());
        String ny = String.valueOf(area.getY());
        String locationName = area.getSido() + " " + area.getSigungu() + " " + area.getEupmyeondong();

        return WeatherForecastResponseDto.builder()
                .adminAreaName(locationName)
                .current(getWeatherDto(nx, ny, "current", 0))
                .after3hours(getWeatherDto(nx, ny, "after3h", 3))
                .tomorrow(getWeatherDto(nx, ny, "tomorrow", 1))
                .dayAfterTomorrow(getWeatherDto(nx, ny, "tomorrow", 2))
                .threeDaysLater(getWeatherDto(nx, ny, "tomorrow", 3))
                .build();
    }

    private WeatherDto getWeatherDto(String nx, String ny, String type, int offset) throws Exception {
        switch (type) {
            case "current" -> {
                return parseWeatherData(fetchUltraSrtNcst(nx, ny), "obsrValue");
            }
            case "after3h" -> {
                return parseWeatherData(fetchUltraSrtFcst(nx, ny, offset), "fcstValue");
            }
            case "tomorrow" -> {
                return parseWeatherData(fetchVilageFcst(nx, ny, offset), "fcstValue");
            }
            default -> throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    // 초단기실황 API 호출 (현재날씨)
    private JsonNode fetchUltraSrtNcst(String nx, String ny) throws Exception {
        LocalDateTime now = LocalDateTime.now().minusHours(1);
        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = now.format(DateTimeFormatter.ofPattern("HH00"));

        StringBuilder urlBuilder = new StringBuilder(ULTRA_SRT_NCST_URL);
        urlBuilder.append("?serviceKey=").append(SERVICE_KEY);
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
    private JsonNode fetchUltraSrtFcst(String nx, String ny, int hourOffset) throws Exception {
        LocalDateTime base = LocalDateTime.now().minusMinutes(30);
        String baseDate = base.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = base.format(DateTimeFormatter.ofPattern("HH30"));

        String targetDate = LocalDateTime.now().plusHours(hourOffset).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String targetTime = LocalDateTime.now().plusHours(hourOffset).format(DateTimeFormatter.ofPattern("HH00"));

        StringBuilder urlBuilder = new StringBuilder(ULTRA_SRT_FCST_URL);
        urlBuilder.append("?serviceKey=").append(SERVICE_KEY);
        urlBuilder.append("&dataType=JSON");
        urlBuilder.append("&numOfRows=1000");
        urlBuilder.append("&pageNo=1");
        urlBuilder.append("&base_date=").append(baseDate);
        urlBuilder.append("&base_time=").append(baseTime);
        urlBuilder.append("&nx=").append(nx);
        urlBuilder.append("&ny=").append(ny);

        JsonNode items = fetchJsonItems(urlBuilder.toString());
        return filterByDateTime(items, targetDate, targetTime);
    }

    // 단기예보 API 호출 (1일 후, 2일 후 날씨)
    private JsonNode fetchVilageFcst(String nx, String ny, int dayOffset) throws Exception {
        String baseDate = LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = "0200";
        String targetDate = LocalDateTime.now().plusDays(dayOffset).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        StringBuilder urlBuilder = new StringBuilder(VILAGE_FCST_URL);
        urlBuilder.append("?serviceKey=").append(SERVICE_KEY);
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
    private JsonNode filterByDateTime(JsonNode items, String date, String time) {
        ArrayNode filtered = objectMapper.createArrayNode();
        for (JsonNode item : items) {
            if (item.get("fcstDate").asText().equals(date) && item.get("fcstTime").asText().equals(time)) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    // 날씨 API 응답에서 실제 필요한 카테고리값을 추출해 WeatherDto를 생성
    // 현재기온, 강수형태, 습도, 풍속, 강수확률
    private WeatherDto parseWeatherData(JsonNode items, String valueField) {
        Map<String, String> map = new HashMap<>();
        for (JsonNode item : items) {
            map.put(item.get("category").asText(), item.get(valueField).asText());
        }

        return WeatherDto.builder()
                .temperature(Double.parseDouble(map.getOrDefault("T1H", map.getOrDefault("TMP", "0"))))
                .weather(WeatherCodeMapper.getWeatherDescription(map.getOrDefault("PTY", "0")))
                .humidity(Integer.parseInt(map.getOrDefault("REH", "0")))
                .windSpeed(Double.parseDouble(map.getOrDefault("WSD", "0")))
                .build();
    }

    // HTTP GET 요청을 보내고, JSON 응답에서 .response.body.items.item 노드를 파싱
    private JsonNode fetchJsonItems(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300
                        ? conn.getInputStream()
                        : conn.getErrorStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();

        return objectMapper.readTree(sb.toString())
                .path("response").path("body").path("items").path("item");
    }







}
