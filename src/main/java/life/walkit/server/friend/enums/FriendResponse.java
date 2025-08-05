package life.walkit.server.friend.enums;

import life.walkit.server.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum FriendResponse implements Response {

    LIST_SUCCESS(HttpStatus.OK, "친구 목록을 조회 성공"),
    STATUS_LIST_SUCCESS(HttpStatus.OK, "상태에 따른 친구 목록 조회 성공"),
    DELETE_SUCCESS(HttpStatus.OK, "친구 삭제 성공"),
    LOCATION_LIST_SUCCESS(HttpStatus.OK, "친구 위치 정보 조회 성공");

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
