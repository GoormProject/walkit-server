package life.walkit.server.member.service;

import life.walkit.server.global.factory.GlobalTestFactory;
import life.walkit.server.global.util.S3Utils;
import life.walkit.server.member.dto.ProfileRequest;
import life.walkit.server.member.entity.Member;
import life.walkit.server.member.entity.enums.MemberRole;
import life.walkit.server.member.entity.enums.MemberStatus;
import life.walkit.server.member.repository.MemberRepository;
import life.walkit.server.member.repository.ProfileImageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProfileImageRepository profileImageRepository;
    @Autowired
    private S3Utils s3Utils;

    @BeforeEach
    void setUp() {
        memberRepository.save(GlobalTestFactory.createMember("user1@test.com", "김유저"));
    }

    @AfterEach
    void tearDown() {
        profileImageRepository.findAll().forEach(profileImage -> {
            s3Utils.deleteFile(profileImage.getProfileImage());
            profileImageRepository.delete(profileImage);
        });
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("새로운 회원을 생성합니다.")
    void createMember_success() {
        // given
        String email = "test@test.com";
        String name = "테스트";
        String profileImage = "";

        // when
        memberService.createMember(email, name, profileImage);

        // then
        Member createdMember = memberRepository.findByEmail(email).orElseThrow();
        assertThat(createdMember)
                .extracting("email", "name", "nickname", "status", "role")
                .containsExactly(email, name, email, MemberStatus.OFFLINE, MemberRole.USER);
    }

    @Test
    @DisplayName("회원 생성시 같은 이메일의 회원이 있으면 반환합니다.")
    void createMember_exists_success() {
        // when
        Member foundMember = memberService.createMember("user1@test.com", "김유저", "");

        // then
        assertThat(foundMember)
                .extracting("email", "nickname")
                .contains("user1@test.com", "김유저");
    }

    @Test
    @DisplayName("프로필을 수정합니다.")
    void updateProfile_success() {
        // given
        Member member = memberRepository.findByEmail("user1@test.com").orElseThrow();

        ProfileRequest request = ProfileRequest.builder()
                .name("새이름")
                .nickname("새닉네임")
                .build();

        MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",                  // 필드 이름
                "profile.jpg",                          // 파일 이름
                "image/jpeg",                           // MIME 타입
                "fake image content".getBytes()         // 파일 내용 (byte[])
        );

        // when
        memberService.updateProfile(member.getMemberId(), request, profileImage);

        // then
        Member foundMember = memberRepository.findByEmail("user1@test.com").orElseThrow();
        assertThat(foundMember)
                .extracting("name", "nickname")
                .containsExactly("새이름", "새닉네임");
        assertThat(foundMember.getProfileImage()).isNotNull();
    }
}
