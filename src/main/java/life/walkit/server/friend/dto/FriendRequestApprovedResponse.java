package life.walkit.server.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendRequestApprovedResponse {
    private Long friendId; // 승인된 친구 ID
}
