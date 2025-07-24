package life.walkit.server.weather.factory;

import life.walkit.server.weather.entity.EupMyeonDong;
import life.walkit.server.weather.entity.Sido;
import life.walkit.server.weather.entity.Sigungu;
import life.walkit.server.weather.entity.Weather;
import java.util.HashMap;
import java.util.Map;

public class WeatherTestFactory {

    public static Weather createWeather(EupMyeonDong emd) {
        return Weather.builder()
                .sido(Sido.GYEONGGI)
                .sigungu(Sigungu.GOYANG_DONGGU)
                .eupmyeondong(emd)
                .current(Map.of("current", "맑음"))
                .forecast(createJsonString("forecast", "맑음"))
                .tomorrow(createJsonString("tomorrow", "구름 많음"))
                .dayAfterTomorrow(createJsonString("dayAfterTomorrow", "비"))
                .temperature(27.5)
                .humidity(65.0)
                .build();
    }

    public static Weather createCustomWeather(
            Sido sido,
            Sigungu sigungu,
            EupMyeonDong emd,
            double temp,
            double humidity
    ) {
        return Weather.builder()
                .sido(sido)
                .sigungu(sigungu)
                .eupmyeondong(emd)
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
        map.put("weather", "맑음");
        return map;
    }

    private static String createJsonString(String key, String value) {
        return String.format("{\"%s\": \"%s\"}", key, value);
    }
}
