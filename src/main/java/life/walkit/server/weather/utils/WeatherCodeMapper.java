package life.walkit.server.weather.utils;

public class WeatherCodeMapper {

    public static String getWeatherDescription(String ptyCode) {
        switch (ptyCode) {
            case "0": return "맑음";
            case "1": return "비";
            case "2": return "비/눈";
            case "3": return "눈";
            case "4": return "소나기";
            default: return "알 수 없음";
        }
    }
}
