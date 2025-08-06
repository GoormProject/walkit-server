package life.walkit.server.review.error.enums;

import life.walkit.server.global.error.core.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰 조회에 실패했습니다."),
    REVIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "본인의 리뷰만 수정할 수 있습니다.");

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
