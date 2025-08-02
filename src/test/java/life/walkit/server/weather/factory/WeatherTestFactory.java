package life.walkit.server.weather.factory;

import life.walkit.server.weather.entity.*;

import java.util.HashMap;
import java.util.Map;

public class WeatherTestFactory {

    public static Weather createWeather(AdminArea area) {
        return Weather.builder()
                .adminArea(area)
                .current(createCurrentWeatherMap())
                .forecast(createJsonString("forecast", "맑음"))
                .tomorrow(createJsonString("tomorrow", "구름 많음"))
                .dayAfterTomorrow(createJsonString("dayAfterTomorrow", "비"))
                .temperature(27.5)
                .humidity(65.0)
                .build();
    }

    public static Weather createCustomWeather(
            AdminArea area,
            double temp,
            double humidity
    ) {
        return Weather.builder()
                .adminArea(area)
                .current(createCurrentWeatherMap())
                .forecast(createJsonString("forecast", "흐림"))
                .tomorrow(createJsonString("tomorrow", "맑음"))
                .dayAfterTomorrow(createJsonString("dayAfterTomorrow", "비"))
                .temperature(temp)
                .humidity(humidity)
                .build();
    }

    private static Map<String, Object> createCurrentWeatherMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("temp", 27.5);
        map.put("humidity", 65);
        map.put("windSpeed", 3.4);
        map.put("current", "맑음");
        return map;
    }

    private static String createJsonString(String key, String value) {
        return String.format("{\"%s\": \"%s\"}", key, value);
    }
}
