package life.walkit.server.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileRequest {

    @NotBlank @Size(max = 20)
    String name;

    @NotBlank @Size(max = 20)
    String nickname;
}
