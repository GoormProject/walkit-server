package life.walkit.server.walk.dto.enums;

import life.walkit.server.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum WalkResponse implements Response {

    START_WALK_SUCCESS(HttpStatus.OK, "산책 기록 시작 성공"),
    PAUSE_WALK_SUCCESS(HttpStatus.OK, "산책 기록 일시정지 성공"),
    RESUME_WALK_SUCCESS(HttpStatus.OK, "산책 기록 재개"),
    END_WALK_SUCCESS(HttpStatus.OK, "산책 종료 성공"),
    CREATE_WALK_SUCCESS(HttpStatus.OK, "산책 기록 등록 생성 성공"),
    GET_WALK_LIST_SUCCESS(HttpStatus.OK, "산책 기록 리스트 조회 성공"),
    DELETE_WALK_SUCCESS(HttpStatus.OK, "산책 기록 삭제 성공");

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
