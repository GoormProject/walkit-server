package life.walkit.server.trail.factory;

import life.walkit.server.member.entity.Member;
import life.walkit.server.member.entity.enums.MemberRole;
import life.walkit.server.member.entity.enums.MemberStatus;
import life.walkit.server.trail.entity.Trail;
import org.locationtech.jts.geom.*;

public class TrailTestFactory {

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    public static Member createMember(String email, String nickname) {
        return Member.builder()
                .email(email)
                .name("김실명")
                .nickname(nickname)
                .status(MemberStatus.OFFLINE)
                .role(MemberRole.USER)
                .build();
    }

    public static Trail createTrail(Member member, String title, String description, Double distance) {
        Point location = createPoint(126.986, 37.541);  // 서울 중심부 근처 좌표
        LineString path = createLineString(new double[][]{
                {126.986, 37.541},
                {126.987, 37.542},
                {126.988, 37.543},
                {126.989, 37.544}
        });

        return Trail.builder()
                .member(member)
                .title(title)
                .description(description)
                .distance(distance)
                .location(location)
                .path(path)
                .build();
    }

    private static Point createPoint(double longitude, double latitude) {
        Coordinate coordinate = new Coordinate(longitude, latitude);
        return geometryFactory.createPoint(coordinate);
    }

    private static LineString createLineString(double[][] coordinates) {
        Coordinate[] coords = new Coordinate[coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            coords[i] = new Coordinate(coordinates[i][0], coordinates[i][1]);
        }
        return geometryFactory.createLineString(coords);
    }

}
