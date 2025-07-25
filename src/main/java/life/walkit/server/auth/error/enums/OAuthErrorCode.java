package life.walkit.server.auth.error.enums;

import life.walkit.server.global.error.core.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum OAuthErrorCode implements ErrorCode {

    KAKAO_OAUTH_ACCOUNT_DATA_MISSING(HttpStatus.BAD_REQUEST, "카카오 OAuth 계정 정보가 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return "[TOKEN ERROR] " + message;
    }

}
