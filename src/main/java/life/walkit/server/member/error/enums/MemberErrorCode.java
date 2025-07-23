package life.walkit.server.member.error.enums;

import life.walkit.server.global.error.core.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    FRIEND_REQUEST_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "상대방과의 친구 신청이 이미 존재합니다."),
    SELF_FRIEND_REQUEST(HttpStatus.BAD_REQUEST, "본인에게 친구 신청할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return "[MEMBER ERROR] " + message;
    }
}
