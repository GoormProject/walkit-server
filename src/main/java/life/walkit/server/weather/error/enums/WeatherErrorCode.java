package life.walkit.server.weather.error.enums;

import life.walkit.server.global.error.core.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum WeatherErrorCode implements ErrorCode {

    WEATHER_JSON_PARSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "기상청 API 응답 파싱 중 오류가 발생하였습니다.");
    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return "[WEATHER ERROR] " + message;
    }
}
