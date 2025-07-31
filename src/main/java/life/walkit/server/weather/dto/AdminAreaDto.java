package life.walkit.server.weather.dto;

import life.walkit.server.global.util.GeoUtils;
import life.walkit.server.weather.entity.AdminArea;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminAreaDto {
    private Long adminAreaId;
    private String sido;
    private String sigungu;
    private String eupmyeondong;
    private Double lng;
    private Double lat;
    private Integer x;
    private Integer y;

    public AdminArea toEntity() {
        if (lng == null || lat == null) {
            throw new IllegalArgumentException("경도와 위도 값이 없습니다.");
        }

        return AdminArea.builder()
                .adminAreaId(adminAreaId)
                .sido(sido)
                .sigungu(sigungu)
                .eupmyeondong(eupmyeondong)
                .location(GeoUtils.toPoint(lng, lat))
                .x(x)
                .y(y)
                .build();
    }
}
