package life.walkit.server.member.entity;

import jakarta.persistence.*;
import life.walkit.server.global.BaseEntity;
import life.walkit.server.member.entity.enums.MemberRole;
import life.walkit.server.member.entity.enums.MemberStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberStatus status;

    @Column(name = "location", columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MemberRole role;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProfileImage profileImage;

    @Builder
    public Member(String email, String name, String nickname, MemberStatus status, Point location, MemberRole role) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.status = status;
        this.location = location;
        this.role = role;
    }

    public void updateProfileText(String name, String nickname) {
        this.name = name;
        this.nickname = nickname;
    }

    public void setProfileImage(ProfileImage profileImage) {
        if (profileImage == null) {
            this.profileImage = null;
            return;
        }
        this.profileImage = profileImage;
        profileImage.setMember(this);
    }

    // 상태 변경 메서드 추가
    public void updateStatus(MemberStatus status) {
        this.status = status;
    }
}
