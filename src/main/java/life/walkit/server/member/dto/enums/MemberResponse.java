package life.walkit.server.member.dto.enums;

import life.walkit.server.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum MemberResponse implements Response {
    GET_PROFILE_SUCCESS(HttpStatus.OK, "내 정보 조회 성공"),
    UPDATE_LOCATION_SUCCESS(HttpStatus.OK, "현재 위치 갱신 성공"),
    UPDATE_PROFILE_SUCCESS(HttpStatus.OK, "프로필 수정 성공");

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
