package life.walkit.server.friend.dto;

import life.walkit.server.friend.enums.FriendRequestStatus;

public record FriendRequestResponseDTO(
        FriendRequestStatus status,
        String senderNickname,
        String receiverNickname,
        Long friendRequestId
) {
}
