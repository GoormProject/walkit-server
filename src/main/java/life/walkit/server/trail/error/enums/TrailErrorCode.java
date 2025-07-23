package life.walkit.server.trail.error.enums;

import life.walkit.server.global.error.core.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum TrailErrorCode implements ErrorCode {
    TRAIL_LIST_SELECT_FAILED(HttpStatus.NOT_FOUND, "산책로 목록 조회 실패"),
    TRAIL_SELECT_FAILED(HttpStatus.NOT_FOUND,"산책로 조회 실패"),
    TRAIL_CREATE_FAILED(HttpStatus.BAD_REQUEST, "산책로 등록 실패");

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
