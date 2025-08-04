package life.walkit.server.walk.dto.request;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.util.List;


public record WalkRequest(
    // 수정 시에만 사용되므로 생성/수정 DTO 분리를 권장합니다.
    Long walkId,

    @Size(
        max = 100,
        message = "산책 제목은 100자를 초과할 수 없습니다."
    )
    @NotBlank(message = "산책 제목은 필수입니다.")
    String walkTitle,

    @NotNull(message = "총 시간은 필수입니다.")
    @Min(value = 0, message = "총 시간은 0 이상이어야 합니다.")
    Integer totalTime,

    @NotNull(message = "총 거리는 필수입니다.")
    @Min(value = 0, message = "총 거리는 0 이상이어야 합니다.")
    Double totalDistance,

    @NotNull(message = "속도는 필수입니다.")
    @Min(value = 0, message = "속도는 0 이상이어야 합니다.")
    Double pace,

    @NotEmpty(message = "경로 데이터는 필수입니다.")
    List<List<Double>> path,

    @NotEmpty(message = "시작 지점은 필수입니다.")
    List<Double> startPoint,

    Long eventId,
    String eventType,

    @URL(message = "유효한 URL 형식이 아닙니다.")
    String routeUrl
) {

}
