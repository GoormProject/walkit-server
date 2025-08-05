package life.walkit.server.weather.error;

import life.walkit.server.global.error.core.BaseException;
import life.walkit.server.global.error.core.ErrorCode;

public class WeatherException extends BaseException {

    public WeatherException(ErrorCode errorCode) {
        super(errorCode);
    }
}
