package life.walkit.server.friend.dto;

import life.walkit.server.member.entity.enums.MemberStatus;

public record SentFriendResponse(
//        String profileImageUrl,
        String receiverNickname,
        MemberStatus memberStatus
) {
}
