package life.walkit.server.walk.error.enums;

import life.walkit.server.global.error.core.BaseException;
import life.walkit.server.global.error.core.ErrorCode;

public class WalkException extends BaseException {

    public WalkException(ErrorCode errorCode) {
        super(errorCode);
    }

}
