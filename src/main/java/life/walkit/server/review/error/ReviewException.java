package life.walkit.server.review.error;

import life.walkit.server.global.error.core.BaseException;
import life.walkit.server.global.error.core.ErrorCode;

public class ReviewException extends BaseException {

    public ReviewException(ErrorCode errorCode) {
        super(errorCode);
    }
}
