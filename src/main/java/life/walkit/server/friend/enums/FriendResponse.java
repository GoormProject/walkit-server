package life.walkit.server.friend.enums;

import life.walkit.server.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum FriendResponse implements Response {

    LIST_SUCCESS(HttpStatus.OK, "친구 목록을 조회하였습니다."),
    DELETE_SUCCESS(HttpStatus.OK, "친구를 삭제하였습니다."),
    FRIEND_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 친구를 찾을 수 없습니다."),
    ALREADY_FRIEND(HttpStatus.CONFLICT, "이미 친구 상태입니다."),
    NOT_FRIEND(HttpStatus.BAD_REQUEST, "친구 상태가 아닙니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
