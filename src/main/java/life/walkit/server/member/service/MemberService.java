package life.walkit.server.member.service;

import life.walkit.server.member.entity.Member;
import life.walkit.server.member.entity.enums.MemberRole;
import life.walkit.server.member.entity.enums.MemberStatus;
import life.walkit.server.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member createMember(String email, String name, String profileImage) {
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

}
