package life.walkit.server.review.dto.enums;

import life.walkit.server.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum TrailReviewResponse implements Response {

    GET_REVIEW_SUCCESS(HttpStatus.OK, "산책로 리뷰 조회 성공"),
    CREATE_REVIEW_SUCCESS(HttpStatus.CREATED, "산책로 리뷰 등록 성공"),
    UPDATE_REVIEW_SUCCESS(HttpStatus.OK, "산책로 리뷰 수정 성공"),
    DELETE_REVIEW_SUCCESS(HttpStatus.OK, "산책로 리뷰 삭제 성공");

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
