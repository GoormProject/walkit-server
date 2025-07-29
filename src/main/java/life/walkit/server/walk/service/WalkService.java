package life.walkit.server.walk.service;

import life.walkit.server.member.entity.Member;
import life.walkit.server.member.error.MemberException;
import life.walkit.server.member.error.enums.MemberErrorCode;
import life.walkit.server.member.repository.MemberRepository;
import life.walkit.server.walk.dto.response.WalkEventResponse;
import life.walkit.server.walk.entity.Walk;
import life.walkit.server.walk.entity.WalkingSession;
import life.walkit.server.walk.entity.enums.EventType;
import life.walkit.server.walk.repository.WalkRepository;
import life.walkit.server.walk.repository.WalkingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalkService {

    private final MemberRepository memberRepository;
    private final WalkRepository walkRepository;
    private final WalkingSessionRepository walkingSessionRepository;

    @Transactional
    public WalkEventResponse startWalk(Long memberId) {

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Walk walk = walkRepository.save(
            Walk.builder()
                .member(member)
                .trail(null)
                .path(null)
                .walkTitle(null)
                .totalDistance(null)
                .totalTime(null)
                .pace(null)
                .isUploaded(false)
                .build()
        );

        WalkingSession walkingSession = walkingSessionRepository.save(
            WalkingSession.builder()
                .walk(walk)
                .eventType(EventType.START)
                .build()
        );

        return WalkEventResponse.from(walkingSession);
    }
}
