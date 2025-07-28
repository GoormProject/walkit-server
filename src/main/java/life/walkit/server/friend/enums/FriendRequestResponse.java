package life.walkit.server.friend.enums;

import life.walkit.server.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum FriendRequestResponse implements Response {
    REQUEST_SUCCESS(HttpStatus.CREATED, "친구 요청 성공"),
    APPROVED_SUCCESS(HttpStatus.OK, "친구 요청 수락 성공"),
    REJECTED_SUCCESS(HttpStatus.OK, "친구 요청 거절 성공"),
    CANCELED_SUCCESS(HttpStatus.OK, "친구 요청 취소 성공."),
    RECEIVED_LIST_SUCCESS(HttpStatus.OK, "받은 친구 요청 목록 조회 성공"),
    SENT_LIST_SUCCESS(HttpStatus.OK, "보낸 친구 요청 목록 조회 성공"),
    LIST_SUCCESS(HttpStatus.OK, "친구 목록 조회 성공.");

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
