package life.walkit.server.weather.factory;

import life.walkit.server.weather.entity.*;

import java.util.HashMap;
import java.util.Map;

public class WeatherTestFactory {

    public static Weather createWeather(AdminArea area) {
        return Weather.builder()
                .adminArea(area)
                .current(createCurrentWeatherMap("맑음"))
                .forecast(createCurrentWeatherMap("흐림"))
                .tomorrow(createCurrentWeatherMap("맑음"))
                .dayAfterTomorrow(createCurrentWeatherMap("비"))
                .threeDaysLater(createCurrentWeatherMap("비"))
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
                .current(createCurrentWeatherMap("맑음"))
                .forecast(createCurrentWeatherMap("흐림"))
                .tomorrow(createCurrentWeatherMap("맑음"))
                .dayAfterTomorrow(createCurrentWeatherMap("비"))
                .threeDaysLater(createCurrentWeatherMap("비"))
                .temperature(temp)
                .humidity(humidity)
                .build();
    }

    private static Map<String, Object> createCurrentWeatherMap(String weather) {
        Map<String, Object> map = new HashMap<>();
        map.put("temperature", 27.5);
        map.put("weather", weather);
        map.put("humidity", 65);
        map.put("windSpeed", 3.4);
        map.put("clouds", 1);
        return map;
    }
}
