package life.walkit.server.trail.error.enums;

import life.walkit.server.global.error.core.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum TrailErrorCode implements ErrorCode {
    TRAIL_LIST_SELECT_FAILED(HttpStatus.NOT_FOUND, "산책로 목록 조회 실패"),
    TRAIL_SELECT_FAILED(HttpStatus.NOT_FOUND, "산책로 조회 실패"),
    TRAIL_CREATE_FAILED(HttpStatus.BAD_REQUEST, "산책로 등록 실패"),
    TRAIL_START_POINT_INVALID(HttpStatus.BAD_REQUEST, "시작 포인터는 정확히 2개의 좌표값이어야 합니다."),
    TRAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 산책로입니다.")
    ;


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
