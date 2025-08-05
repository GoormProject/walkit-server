package life.walkit.server.friend.dto;

import life.walkit.server.member.dto.LocationDto;
import life.walkit.server.member.entity.Member;
import life.walkit.server.member.entity.ProfileImage;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
@Builder
public class FriendLocationResponseDTO {
    private Long friendId;       // 친구 ID
    private Long memberId;       // 멤버 ID
    private String nickname;     // 닉네임
    private String profile;      // 프로필 이미지 URL
    private LocationDto location; // 위치 정보 DTO

    public static FriendLocationResponseDTO of(Member member, Long friendId) {
        return FriendLocationResponseDTO.builder()
                .friendId(friendId)
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .profile(Optional.ofNullable(member.getProfileImage())
                        .map(ProfileImage::getProfileImage)
                        .orElse("")) // null-safe
                .location(LocationDto.builder()
                        .lng(member.getLocation().getX()) // 경도
                        .lat(member.getLocation().getY()) // 위도
                        .build())
                .build();
    }



}
