package life.walkit.server.weather.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class WeatherDto {
    private double temperature;
    private String weather;
    private int humidity;
    private double windSpeed;
    private int clouds;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("temperature", temperature);
        map.put("weather", weather);
        map.put("humidity", humidity);
        map.put("windSpeed", windSpeed);
        map.put("clouds", clouds);
        return map;
    }

    public static WeatherDto of(Map<String, Object> map) {
        return WeatherDto.builder()
                .temperature(toDouble(map.get("temperature")))
                .weather((String) map.get("weather"))
                .humidity(toInt(map.get("humidity")))
                .windSpeed(toDouble(map.get("windSpeed")))
                .clouds(toInt(map.get("clouds")))
                .build();
    }

    private static double toDouble(Object value) {
        if (value instanceof Number num) return num.doubleValue();
        if (value instanceof String str) return Double.parseDouble(str);
        throw new IllegalArgumentException("Invalid double value: " + value);
    }

    private static int toInt(Object value) {
        if (value instanceof Number num) return num.intValue();
        if (value instanceof String str) return Integer.parseInt(str);
        throw new IllegalArgumentException("Invalid int value: " + value);
    }
}
