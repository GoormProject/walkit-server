package life.walkit.server.trail.error.enums;

import life.walkit.server.global.error.core.BaseException;
import life.walkit.server.global.error.core.ErrorCode;

public class TrailException extends BaseException {

    public TrailException(ErrorCode errorCode) {
        super(errorCode);
    }
}
