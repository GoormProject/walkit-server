package life.walkit.server.global.factory;

import life.walkit.server.friend.entity.Friend;
import life.walkit.server.friend.entity.FriendRequest;
import life.walkit.server.member.entity.Member;
import life.walkit.server.member.entity.enums.MemberRole;
import life.walkit.server.member.entity.enums.MemberStatus;
import life.walkit.server.path.entity.Path;
import life.walkit.server.trail.entity.Trail;
import life.walkit.server.trailwalkimage.entity.TrailWalkImage;
import life.walkit.server.walk.entity.Walk;
import life.walkit.server.walk.entity.WalkingSession;
import life.walkit.server.walk.entity.enums.EventType;
import org.locationtech.jts.geom.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GlobalTestFactory {

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    public static Member createMember(
        String email,
        String nickname
    ) {
        return Member.builder()
            .email(email)
            .name("김실명")
            .nickname(nickname)
            .role(MemberRole.USER)
            .status(MemberStatus.ONLINE)
            .build();
    }

    public static Friend createFriend(
        Member member,
        Member partner
    ) {
        return Friend.builder()
            .member(member)
            .partner(partner)
            .build();
    }

    public static FriendRequest createFriendRequest(
        Member sender,
        Member receiver
    ) {
        return FriendRequest.builder()
            .sender(sender)
            .receiver(receiver)
            .build();
    }

    public static Trail createTrail(
        Member member,
        String title,
        String description,
        Double distance,
        String location,
        Path path
    ) {

        return Trail.builder()
            .member(member)
            .title(title)
            .description(description)
            .distance(distance)
            .location(location)
            .path(path)
            .build();
    }

    public static Walk createWalk(
        Member member,
        Trail trail,
        Path path,
        String walkTitle,
        Double totalDistance,
        Duration totalTime,
        Double pace,
        Boolean isUploaded
    ) {

        return Walk.builder()
            .member(member)
            .trail(trail)
            .path(path)
            .walkTitle(walkTitle)
            .totalDistance(totalDistance)
            .totalTime(totalTime)
            .pace(pace)
            .isUploaded(isUploaded)
            .build();
    }

    public static WalkingSession createWalkingSession(
        Walk walk,
        EventType eventType
    ) {
        return WalkingSession.builder()
            .walk(walk)
            .eventType(eventType)
            .build();
    }

    public static Path createPath(Double[][] lineString) {
        return Path.builder()
            .path(createLineString(lineString))
            .point(createPoint())
            .build();
    }

    private static Point createPoint() {
        Coordinate coordinate = new Coordinate(126.986, 37.541);
        return geometryFactory.createPoint(coordinate);
    }

    private static LineString createLineString(Double[][] lineString) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        Coordinate[] coordinates = new Coordinate[lineString.length];
        for (int i = 0; i < lineString.length; i++) {
            coordinates[i] = new Coordinate(lineString[i][0], lineString[i][1]);
        }

        return geometryFactory.createLineString(coordinates);
    }

    public static TrailWalkImage createTrailWalkImage(
        String imageUrl,
        Trail trail,
        Walk walk
    ) {
        return TrailWalkImage.builder()
            .trail(trail)
            .walk(walk)
            .routeImage(imageUrl)
            .build();
    }


}
