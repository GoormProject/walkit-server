package life.walkit.server.member.entity;

import jakarta.persistence.*;
import life.walkit.server.global.BaseEntity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_profile_image")
public class ProfileImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_image_id")
    private Long profileImageId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    Member member;

    @Column(name = "profile", nullable = false)
    String profileImage;

    @Builder
    public ProfileImage(Member member, String profileImage) {
        this.member = member;
        this.profileImage = profileImage;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
