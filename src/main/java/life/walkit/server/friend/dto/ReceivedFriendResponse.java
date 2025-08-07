package life.walkit.server.friend.dto;

import life.walkit.server.friend.enums.FriendRequestStatus;
import life.walkit.server.member.entity.Member;
import life.walkit.server.member.entity.ProfileImage;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReceivedFriendResponse {
    private Long friendRequestId;
    private String senderNickname;
    private String profile;
    private FriendRequestStatus requestStatus;

    public static ReceivedFriendResponse of(Long friendRequestId, Member sender, FriendRequestStatus requestStatus) {
        return ReceivedFriendResponse.builder()
                .friendRequestId(friendRequestId)
                .senderNickname(sender.getNickname())
                .profile(Optional.ofNullable(sender.getProfileImage())
                        .map(ProfileImage::getProfileImage)
                        .orElse(""))
                .requestStatus(requestStatus)
                .build();
    }
}

