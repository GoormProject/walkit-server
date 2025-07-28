package life.walkit.server.member.error.enums;

import life.walkit.server.global.error.core.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    EMAIL_NOT_VALID(HttpStatus.BAD_REQUEST, "이메일이 null 또는 빈 문자열입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    MEMBER_FORBIDDEN(HttpStatus.NOT_FOUND, "자신의 프로필만 수정할 수 있습니다."),
    MEMBER_PROFILE_IMAGE_IO_FAILED(HttpStatus.NOT_FOUND, "프로필 이미지 업로드 중 IO 오류가 발생하였습니다."),
    FRIEND_REQUEST_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "상대방과의 친구 신청이 이미 존재합니다."),
    SELF_FRIEND_REQUEST(HttpStatus.BAD_REQUEST, "본인에게 친구 신청할 수 없습니다."),
    FRIEND_STATUS_INVALID(HttpStatus.BAD_REQUEST, "친구 신청 승인 또는 거절시 상태가 잘못되었습니다.");

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
