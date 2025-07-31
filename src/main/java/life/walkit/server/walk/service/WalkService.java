package life.walkit.server.walk.service;

import life.walkit.server.member.entity.Member;
import life.walkit.server.member.error.MemberException;
import life.walkit.server.member.error.enums.MemberErrorCode;
import life.walkit.server.member.repository.MemberRepository;
import life.walkit.server.path.entity.Path;
import life.walkit.server.trailwalkimage.entity.TrailWalkImage;
import life.walkit.server.trailwalkimage.repository.TrailWalkImageRepository;
import life.walkit.server.walk.dto.request.WalkRequest;
import life.walkit.server.walk.dto.response.WalkCreateResponse;
import life.walkit.server.walk.dto.response.WalkDeleteResponse;
import life.walkit.server.walk.dto.response.WalkEventResponse;
import life.walkit.server.walk.dto.response.WalkListResponse;
import life.walkit.server.walk.entity.Walk;
import life.walkit.server.walk.entity.WalkingSession;
import life.walkit.server.walk.entity.enums.EventType;
import life.walkit.server.walk.error.enums.WalkErrorCode;
import life.walkit.server.walk.error.enums.WalkException;
import life.walkit.server.walk.repository.WalkRepository;
import life.walkit.server.walk.repository.WalkingSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.locationtech.jts.geom.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalkService {

    private final MemberRepository memberRepository;
    private final WalkRepository walkRepository;
    private final WalkingSessionRepository walkingSessionRepository;
    private final TrailWalkImageRepository trailWalkImageRepository;

    @Transactional
    public WalkEventResponse startWalk(Long memberId) {

        Member member = findByMemberId(memberId);

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
        WalkingSession latestSession = findLatestSessionByWalk(walk);

        // 이미 끝난 산책기록이면 에러 발생
        validateNotCompleted(latestSession);

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
        WalkingSession latestSession = findLatestSessionByWalk(walk);

        // PAUSE 일때만 시작 가능
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

    @Transactional
    public WalkEventResponse endWalk(Long walkId) {
        Walk walk = findByWalkId(walkId);
        WalkingSession latestSession = findLatestSessionByWalk(walk);

        // 이미 끝난 산책기록이면 에러 발생
        validateNotCompleted(latestSession);

        WalkingSession walkingSession = walkingSessionRepository.save(
            WalkingSession.builder()
                .walk(walk)
                .eventType(EventType.END)
                .build()
        );

        return WalkEventResponse.from(walkingSession);
    }

    @Transactional
    public WalkCreateResponse createWalk(WalkRequest walkRequest) {
        Walk walk = findByWalkId(walkRequest.walkId());

        WalkingSession latestSessionByWalk = findLatestSessionByWalk(walk);

        // 중간 세션이 끝난 상태가 아닐시 끝낸다.
        if (latestSessionByWalk.getEventType() != EventType.END) {
            if (walkRequest.eventType().equals("END")) {
                latestSessionByWalk.updateWalkingSessionEventType(EventType.END);
            }
        }

        LineString lineString = createLineString(walkRequest.path());
        Point point = createPath(walkRequest.startPoint());

        Path newPath = Path.builder()
            .path(lineString)
            .point(point)
            .build();

        Duration totalDuration = Duration.ofSeconds(walkRequest.totalTime());

        walk.updateWalkDetails(
            walkRequest.walkTitle(),
            newPath,
            walkRequest.totalDistance(),
            totalDuration,
            walkRequest.pace()
        );

        trailWalkImageRepository.save(
            TrailWalkImage.builder()
                .trail(null)
                .routeImage(walkRequest.routeUrl())
                .walk(walk)
                .build()
        );
        Walk savedWalk = walkRepository.save(walk);

        return new WalkCreateResponse(savedWalk.getWalkId());
    }

    @Transactional(readOnly = true)
    public List<WalkListResponse> getWalkList(Long memberId) {
        Member member = findByMemberId(memberId);
        List<Walk> walkList = walkRepository.findByMember(member);

        return walkList.stream()
            .map(this::mapToWalkListResponse)
            .toList();
    }

    @Transactional
    public WalkDeleteResponse deleteWalk(Long walkId) {
        Walk foundWalk = findByWalkId(walkId);
        walkRepository.delete(foundWalk);
        Long foundWalkId = foundWalk.getWalkId();
        Long memberId = foundWalk.getMember().getMemberId();

        return new WalkDeleteResponse(foundWalkId, memberId);
    }

    private WalkListResponse mapToWalkListResponse(Walk walk) {
        WalkingSession latestSession = findLatestSessionByWalk(walk);
        List<TrailWalkImage> trailWalkImages = trailWalkImageRepository.findByWalk(walk);

        Long imageId = null;
        String imageUrl = null;
        if (!trailWalkImages.isEmpty()) {
            TrailWalkImage firstImage = trailWalkImages.get(0);
            imageId = firstImage.getTrailImageId();
            imageUrl = firstImage.getRouteImage();
        }

        return new WalkListResponse(
            walk.getWalkId(),
            walk.getTrail() != null ? walk.getTrail().getTrailId() : null,
            latestSession.getEventId(),
            latestSession.getEventTime().toString(),
            imageId,
            imageUrl,
            walk.getTotalDistance(),
            walk.getTotalTime() != null ? walk.getTotalTime().toString() : null,
            walk.getPace() != null ? walk.getPace().toString() : null,
            walk.getWalkTitle(),
            walk.getIsUploaded()
        );
    }

    private LineString createLineString(List<List<Double>> pathPoints) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        if (pathPoints == null || pathPoints.isEmpty()) {
            return geometryFactory.createLineString();
        }

        Coordinate[] coordinates = pathPoints.stream()
            .map(point -> new Coordinate(point.get(0), point.get(1))) // 각 List<Double>을 Coordinate 객체로 변환
            .toArray(Coordinate[]::new); // Coordinate 배열로 최종 변환

        return geometryFactory.createLineString(coordinates);
    }

    private Point createPath(List<Double> pathPoint) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        if (pathPoint == null || pathPoint.size() < 2) {
            throw new IllegalArgumentException("좌표 데이터는 최소 2개(경도, 위도)가 필요합니다.");
        }

        Coordinate coordinate = new Coordinate(pathPoint.get(0), pathPoint.get(1));
        return geometryFactory.createPoint(coordinate);
    }

    private Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private Walk findByWalkId(Long walkId) {
        return walkRepository.findById(walkId)
            .orElseThrow(() -> new WalkException(WalkErrorCode.WALK_NOT_FOUND));
    }

    private WalkingSession findLatestSessionByWalk(Walk walk) {
        return walkingSessionRepository.findFirstByWalkOrderByEventTimeDesc(walk)
            .orElseThrow(() -> new WalkException(WalkErrorCode.WALK_NOT_FOUND));
    }

    private void validateNotCompleted(WalkingSession latestSession) {
        if (latestSession.getEventType() == EventType.END) {
            throw new WalkException(WalkErrorCode.WALK_ALREADY_COMPLETED);
        }
    }
}
