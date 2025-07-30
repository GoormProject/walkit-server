package life.walkit.server.friend.dto;

import life.walkit.server.member.dto.LocationDto;
import life.walkit.server.member.entity.Member;
import life.walkit.server.member.entity.ProfileImage;
import life.walkit.server.member.entity.enums.MemberStatus;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FriendResponseDTO {
    private Long friendId;
    private String nickname;
    private String profile;
    private MemberStatus memberStatus;
    private LocationDto lastLocation;

    public static FriendResponseDTO of(Member member, LocationDto lastLocation) {
        return FriendResponseDTO.builder()
                .friendId(member.getMemberId())
                .nickname(member.getNickname())
                .profile(Optional.ofNullable(member.getProfileImage())
                        .map(ProfileImage::getProfileImage)
                        .orElse(""))
                .memberStatus(member.getStatus())
                .lastLocation(lastLocation)
                .build();
    }
}

