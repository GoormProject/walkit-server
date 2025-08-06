package life.walkit.server.global.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class BaseResponse<T> {

    private final Integer httpStatus;
    private final String message;
    private final T data;

    public static ResponseEntity<BaseResponse<Void>> toResponseEntity(Response response) {
        return ResponseEntity.status(response.getHttpStatus())
                .body(BaseResponse.<Void>builder()
                        .httpStatus(response.getHttpStatus().value())
                        .message(response.getMessage())
                        .build());
    }

    public static <T> ResponseEntity<BaseResponse<Void>> toResponseEntity(HttpStatus httpStatus, String message) {
       return ResponseEntity.status(httpStatus)
                .body(BaseResponse.<Void>builder()
                        .httpStatus(httpStatus.value())
                        .message(message)
                        .build());
    }

    public static <T> ResponseEntity<BaseResponse<T>> toResponseEntity(Response response, T data) {
        return ResponseEntity.status(response.getHttpStatus())
                .body(BaseResponse.<T>builder()
                        .httpStatus(response.getHttpStatus().value())
                        .message(response.getMessage())
                        .data(data)
                        .build());
    }

    public static <T> ResponseEntity<BaseResponse<T>> toResponseEntity(HttpStatus httpStatus, String message, T data) {
        return ResponseEntity.status(httpStatus)
                .body(BaseResponse.<T>builder()
                        .httpStatus(httpStatus.value())
                        .message(message)
                        .data(data)
                        .build());
    }
}
