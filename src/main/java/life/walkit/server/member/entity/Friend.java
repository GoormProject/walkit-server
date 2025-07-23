package life.walkit.server.member.entity;

import jakarta.persistence.*;
import life.walkit.server.global.BaseEntity;
import life.walkit.server.member.entity.enums.FriendStatus;
import life.walkit.server.member.error.MemberException;
import life.walkit.server.member.error.enums.MemberErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "friend",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "partner_id"})
    }
)
public class Friend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_id")
    private Long friendId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Member partner;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FriendStatus status;

    @Builder
    public Friend(Member member, Member partner) {
        if (member.equals(partner)) {
            throw new MemberException(MemberErrorCode.SELF_FRIEND_REQUEST);
        }
        this.member = member;
        this.partner = partner;
        this.status = FriendStatus.PENDING;
    }

    public void approve() {
        this.status = FriendStatus.APPROVED;
    }

    public void reject() {
        this.status = FriendStatus.REJECTED;
    }
}
