package life.walkit.server.global.response;

import org.springframework.http.HttpStatus;

public interface Response {

    HttpStatus getHttpStatus();
    String getMessage();
}
