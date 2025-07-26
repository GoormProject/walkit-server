package life.walkit.server.friendrequest.entity;

import jakarta.persistence.*;
import life.walkit.server.member.entity.Member;
import life.walkit.server.member.error.MemberException;
import life.walkit.server.member.error.enums.MemberErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "friend_request")
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendRequestId;

    @ManyToOne
    private Member sender;

    @ManyToOne
    private Member receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendRequestStatus status;

    @Builder
    public FriendRequest(Member sender, Member receiver) {
        if (sender.equals(receiver)) {
            throw new MemberException(MemberErrorCode.SELF_FRIEND_REQUEST);
        }
        this.sender = sender;
        this.receiver = receiver;
        this.status = FriendRequestStatus.PENDING;
    }

    public void approve() {
        if (this.status != FriendRequestStatus.PENDING && this.status != FriendRequestStatus.REJECTED) {
            throw new MemberException(MemberErrorCode.FRIEND_STATUS_INVALID);
        }
        this.status = FriendRequestStatus.APPROVED;
    }

    public void reject() {
        if (this.status != FriendRequestStatus.PENDING) {
            throw new MemberException(MemberErrorCode.FRIEND_STATUS_INVALID);
        }
        this.status = FriendRequestStatus.REJECTED;
    }

    public void cancel() {
        if (this.status != FriendRequestStatus.PENDING) {
            throw new MemberException(MemberErrorCode.FRIEND_STATUS_INVALID);
        }
        this.status = FriendRequestStatus.CANCELLED;
    }

}
