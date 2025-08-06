package life.walkit.server.member.error;

import life.walkit.server.global.error.core.BaseException;
import life.walkit.server.global.error.core.ErrorCode;

public class MemberException extends BaseException {

    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
