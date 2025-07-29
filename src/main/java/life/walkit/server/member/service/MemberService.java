package life.walkit.server.member.service;

import life.walkit.server.auth.repository.LastActiveRepository;
import life.walkit.server.global.util.S3Utils;
import life.walkit.server.member.dto.ProfileRequest;
import life.walkit.server.member.dto.ProfileResponse;
import life.walkit.server.member.entity.Member;
import life.walkit.server.member.entity.ProfileImage;
import life.walkit.server.member.entity.enums.MemberRole;
import life.walkit.server.member.entity.enums.MemberStatus;
import life.walkit.server.member.error.MemberException;
import life.walkit.server.member.error.enums.MemberErrorCode;
import life.walkit.server.member.repository.MemberRepository;
import life.walkit.server.member.repository.ProfileImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ProfileImageRepository profileImageRepository;
    private final LastActiveRepository lastActiveRepository;
    private final S3Utils s3Utils;

    private static final String IMAGE_PATH = "profile/";

    @Transactional
    public Member createMember(String email, String name, String profileImage) {
        if (email == null || email.trim().isEmpty()) {
            throw new MemberException(MemberErrorCode.EMAIL_NOT_VALID);
        }

        return memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .email(email)
                            .name(name)
                            .nickname(email)
                            .status(MemberStatus.OFFLINE)
                            .role(MemberRole.USER)
                            .build();
                    return memberRepository.save(newMember);
                });
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return ProfileResponse.of(member);
    }

    @Transactional
    public MemberStatus refreshMemberStatus(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (member.getStatus() == MemberStatus.WALKING)
            return member.getStatus();

        if (lastActiveRepository.findByKey(member.getMemberId().toString()).isPresent())
            member.updateStatus(MemberStatus.ONLINE);
        else
            member.updateStatus(MemberStatus.OFFLINE);

        memberRepository.save(member);
        return member.getStatus();
    }

    @Transactional
    public ProfileResponse updateProfile(Long memberId, ProfileRequest request, MultipartFile image) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 이름, 닉네임 변경
        member.updateProfileText(request.getName(), request.getNickname());

        // 프로필 이미지 변경
        if (image != null) {
            try {
                // 기존 이미지 삭제
                ProfileImage profileImage = member.getProfileImage();
                if (profileImage != null) {
                    s3Utils.deleteFile(profileImage.getProfileImage());
                }

                // 새 이미지 업로드 및 저장
                if (profileImage == null) {
                    profileImage = ProfileImage.builder()
                            .member(member)
                            .profileImage(s3Utils.uploadFileAndGetUrl(IMAGE_PATH, image))
                            .build();
                }
                else {
                    profileImage.setProfileImage(s3Utils.uploadFileAndGetUrl(IMAGE_PATH, image));
                }

                profileImage = profileImageRepository.save(profileImage);
                member.setProfileImage(profileImage);
                
            } catch (IOException e) {
                throw new MemberException(MemberErrorCode.MEMBER_PROFILE_IMAGE_IO_FAILED);
            }
        }

        return ProfileResponse.of(member);
    }
}
