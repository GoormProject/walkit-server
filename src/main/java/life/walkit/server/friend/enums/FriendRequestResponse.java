package life.walkit.server.friend.enums;

import life.walkit.server.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum FriendRequestResponse implements Response {
    APPROVED_SUCCESS(HttpStatus.OK, "친구 요청을 수락하였습니다."),
    REJECTED_SUCCESS(HttpStatus.OK, "친구 요청을 거절하였습니다."),
    CANCELED_SUCCESS(HttpStatus.OK, "친구 요청을 취소하였습니다."),
    RECEIVED_LIST_SUCCESS(HttpStatus.OK, "받은 친구 요청 목록을 조회하였습니다."),
    SENT_LIST_SUCCESS(HttpStatus.OK, "보낸 친구 요청 목록을 조회하였습니다."),
    LIST_SUCCESS(HttpStatus.OK, "친구 목록을 조회하였습니다.");

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
