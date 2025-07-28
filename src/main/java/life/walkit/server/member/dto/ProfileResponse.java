package life.walkit.server.member.dto;

import life.walkit.server.member.entity.Member;
import life.walkit.server.member.entity.ProfileImage;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
@Builder
public class ProfileResponse {

    String name;
    String nickname;
    String email;
    String profile;

    public static ProfileResponse of(Member member) {
        return ProfileResponse.builder()
                .name(member.getName())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .profile(Optional.ofNullable(member.getProfileImage())
                        .map(ProfileImage::getProfileImage)
                        .orElse(""))
                .build();
    }
}
