package life.walkit.server.global.error.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final int httpStatus;
    private final String message;
    private final String originalErrorBody;

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getStatus().value(), errorCode.getMessage(), null);
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(errorCode.getStatus().value(), message, null);
    }

    public static ErrorResponse of(ErrorCode errorCode, String message, String originalErrorBody) {
        return new ErrorResponse(errorCode.getStatus().value(), message, originalErrorBody);
    }
}
