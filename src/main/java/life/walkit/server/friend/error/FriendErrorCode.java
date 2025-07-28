package life.walkit.server.friend.error;

import life.walkit.server.global.error.core.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum FriendErrorCode implements ErrorCode {
    ALREADY_FRIEND(HttpStatus.BAD_REQUEST, "이미 친구 상태입니다."),
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "친구 요청이 존재하지 않습니다."),
    FRIEND_REQUEST_BLOCKED(HttpStatus.FORBIDDEN, "차단된 사용자에게 친구 요청을 보낼 수 없습니다."),
    CANNOT_UNFRIEND_SELF(HttpStatus.BAD_REQUEST, "자기 자신을 친구 삭제할 수 없습니다."),
    FRIEND_NOT_FOUND(HttpStatus.NOT_FOUND, "친구 관계가 존재하지 않습니다."),
    FRIEND_RELATIONSHIP_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "친구 관계 생성에 실패했습니다."),
    FRIEND_RELATIONSHIP_DELETION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "친구 관계 삭제에 실패했습니다."),
    REQUEST_RECEIVER_MISMATCH(HttpStatus.BAD_REQUEST, "현재 사용자가 해당 요청의 수신자가 아닙니다."),
    INVALID_REQUEST_STATUS(HttpStatus.BAD_REQUEST, "친구 요청 상태가 유효하지 않습니다."),
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
        return "[FRIEND ERROR] " + message;
    }

}
