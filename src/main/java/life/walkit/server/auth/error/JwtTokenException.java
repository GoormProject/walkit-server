package life.walkit.server.auth.error;

import life.walkit.server.global.error.core.BaseException;
import life.walkit.server.global.error.core.ErrorCode;

public class JwtTokenException extends BaseException {
    public JwtTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
