package life.walkit.server.review.dto.request;

import jakarta.validation.constraints.*;

public record ReviewUpdateRequest(

        @NotBlank(message = "리뷰 내용을 입력해주세요.")
        @Size(max = 2000, message = "리뷰 내용은 2000자를 초과할 수 없습니다.")
        String content,

        @NotNull(message = "평점을 입력해주세요.")
        @Min(value = 1, message = "최소 평점은 1입니다.")
        @Max(value = 5, message = "최대 평점은 5입니다.")
        Integer rating

) {
}
