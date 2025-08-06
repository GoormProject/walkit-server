package life.walkit.server.trail.dto.enums;

import life.walkit.server.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum TrailResponse implements Response {

    TRAIL_CREATE_SUCCESS(HttpStatus.CREATED, "산책로 생성 성공"),
    TRAIL_DETAIL_SELECT_SUCCESS(HttpStatus.OK, "산책로 상세 조회 성공"),
    GET_TRAIL_LIST_SUCCESS(HttpStatus.OK, "산책로 목록 조회 성공");

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
