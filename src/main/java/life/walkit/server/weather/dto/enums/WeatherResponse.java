package life.walkit.server.weather.dto.enums;

import life.walkit.server.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum WeatherResponse implements Response {
    GET_NEAR_WEATHER(HttpStatus.OK, "주변 날씨 조회 성공"),
    GET_CLOTH_RECOMMENDATION(HttpStatus.OK, "맞춤 옷차림 조회 성공");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
