package life.walkit.server.walk.error.enums;

import life.walkit.server.global.error.core.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum WalkErrorCode implements ErrorCode {
    WALK_START_FAILED(HttpStatus.BAD_REQUEST, "산책 기록 시작에 실패했습니다"),
    WALK_PAUSE_FAILED(HttpStatus.BAD_REQUEST, "산책 기록 일시정지에 실패했습니다"),
    WALK_RESUME_FAILED(HttpStatus.BAD_REQUEST, "산책 기록 재개에 실패했습니다"),
    WALK_LIST_SELECT_FAILED(HttpStatus.NOT_FOUND, "산책 기록 목록 조회에 실패했습니다"),
    WALK_SELECT_FAILED(HttpStatus.NOT_FOUND, "산책 기록 상세 조회에 실패했습니다"),
    WALK_DELETE_FAILED(HttpStatus.BAD_REQUEST, "산책 기록 삭제에 실패했습니다"),
    WALK_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 산책 기록입니다"),
    WALK_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "이미 완료된 산책 기록입니다"),
    WALK_NOT_IN_PROGRESS(HttpStatus.BAD_REQUEST, "진행 중인 산책 기록이 아닙니다"),
    WALK_ALREADY_PAUSED(HttpStatus.BAD_REQUEST, "이미 일시정지된 산책 기록입니다");

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
