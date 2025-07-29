package life.walkit.server.walk.service;

import life.walkit.server.member.entity.Member;
import life.walkit.server.member.error.MemberException;
import life.walkit.server.member.error.enums.MemberErrorCode;
import life.walkit.server.member.repository.MemberRepository;
import life.walkit.server.walk.dto.response.WalkEventResponse;
import life.walkit.server.walk.entity.Walk;
import life.walkit.server.walk.entity.WalkingSession;
import life.walkit.server.walk.entity.enums.EventType;
import life.walkit.server.walk.error.enums.WalkErrorCode;
import life.walkit.server.walk.error.enums.WalkException;
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

    @Transactional
    public WalkEventResponse pauseWalk(Long walkId) {

        Walk walk = findByWalkId(walkId);

        // 이미 끝난 산책기록이면 에러 발생
        walkingSessionRepository.findByWalk(walk)
            .stream()
            .filter(item -> item.getEventType() == EventType.END)
            .findFirst()
            .ifPresent(invalidItem -> {
                throw new WalkException(WalkErrorCode.WALK_ALREADY_COMPLETED);
            });

        WalkingSession walkingSession = walkingSessionRepository.save(
            WalkingSession.builder()
                .walk(walk)
                .eventType(EventType.PAUSE)
                .build()
        );

        return WalkEventResponse.from(walkingSession);
    }

    @Transactional
    public WalkEventResponse resumeWalk(Long walkId) {

        Walk walk = findByWalkId(walkId);

        // PAUSE 일때만 시작 가능
        WalkingSession latestSession = walkingSessionRepository.findFirstByWalkOrderByEventTimeDesc(walk)
            .orElseThrow(() -> new WalkException(WalkErrorCode.WALK_NOT_FOUND));

        if (latestSession.getEventType() != EventType.PAUSE) {
            throw new WalkException(WalkErrorCode.WALK_NOT_PAUSED);
        }

        WalkingSession walkingSession = walkingSessionRepository.save(
            WalkingSession.builder()
                .walk(walk)
                .eventType(EventType.RESUME)
                .build()
        );

        return WalkEventResponse.from(walkingSession);
    }


    private Walk findByWalkId(Long walkId) {
        return walkRepository.findById(walkId)
            .orElseThrow(() -> new WalkException(WalkErrorCode.WALK_NOT_FOUND));
    }
}
