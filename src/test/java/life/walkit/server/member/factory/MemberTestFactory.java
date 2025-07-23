package life.walkit.server.member.factory;

import life.walkit.server.member.entity.Friend;
import life.walkit.server.member.entity.Member;
import life.walkit.server.member.entity.enums.MemberRole;
import life.walkit.server.member.entity.enums.MemberStatus;

public class MemberTestFactory {

    public static Member createMember(String email, String nickname) {
        return Member.builder()
                .email(email)
                .name("김실명")
                .nickname(nickname)
                .status(MemberStatus.OFFLINE)
                .role(MemberRole.USER)
                .build();
    }

    public static Friend createFriend(Member member, Member partner) {
        return Friend.builder()
                .member(member)
                .partner(partner)
                .build();
    }

    public static Friend createFriendApproved(Member member, Member partner) {
        Friend friend = Friend.builder()
                .member(member)
                .partner(partner)
                .build();
        friend.approve();
        return friend;
    }
}
