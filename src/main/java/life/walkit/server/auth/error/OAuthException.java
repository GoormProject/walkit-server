package life.walkit.server.auth.error;

import life.walkit.server.global.error.core.BaseException;
import life.walkit.server.global.error.core.ErrorCode;

public class OAuthException extends BaseException {
    public OAuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
