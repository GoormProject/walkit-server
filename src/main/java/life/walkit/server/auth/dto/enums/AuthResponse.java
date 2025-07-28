package life.walkit.server.auth.dto.enums;

import life.walkit.server.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AuthResponse implements Response {
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃 성공"),
    ACCESS_TOKEN_REISSUE_SUCCESS(HttpStatus.OK, "액세스 토큰 재발급 성공"),
    GET_CURRENT_MEMBER_SUCCESS(HttpStatus.OK, "본인확인 성공");

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
