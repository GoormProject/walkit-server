package life.walkit.server.review.error.enums;

import life.walkit.server.global.error.core.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {
    REVIEW_SELECT_FAILED(HttpStatus.NOT_FOUND, "리뷰 조회에 실패했습니다."),
    REVIEW_CREATE_FAILED(HttpStatus.BAD_REQUEST, "리부 생성에 실패했습니다."),
    REVIEW_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "리뷰 수정에 실패했습니다."),
    REVIEW_DELETE_FAILED(HttpStatus.BAD_REQUEST, "리뷰 삭제에 실패했습니다.");

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
