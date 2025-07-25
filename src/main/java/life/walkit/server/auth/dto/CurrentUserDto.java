package life.walkit.server.auth.dto;

import life.walkit.server.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentUserDto {

    Long memberId;
    String email;
    Boolean isProfileSet;

    public static CurrentUserDto of(Member member) {
        return CurrentUserDto.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .isProfileSet(!member.getEmail().equals(member.getNickname()))
                .build();
    }
}
