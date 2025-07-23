package life.walkit.server.weather.entity;

import jakarta.persistence.*;
import life.walkit.server.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Getter
@Table(name = "weather")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Weather extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weather_id")
    private Long weatherId;

    @Enumerated(EnumType.STRING)
    @Column(name = "sido", nullable = false)
    private Sido sido;

    @Enumerated(EnumType.STRING)
    @Column(name = "sigungu", nullable = false)
    private Sigungu sigungu;

    @Enumerated(EnumType.STRING)
    @Column(name = "eupmyeondong", nullable = false)
    private EupMyeonDong eupmyeondong;

    @Column(name = "current", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> current;

    @Column(name = "forecast", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String forecast;

    @Column(name = "tomorrow", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String tomorrow;

    @Column(name = "dayAfterTomorrow", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String dayAfterTomorrow;

    @Column(name = "temperature", nullable = false)
    private double temperature;

    @Column(name = "humidity", nullable = false)
    private double humidity;

    @Builder
    public Weather(Sido sido, Sigungu sigungu, EupMyeonDong eupmyeondong,
                   Map<String, Object> current, String forecast, String tomorrow, String dayAfterTomorrow, double temperature, double humidity) {
        this.sido = sido;
        this.sigungu = sigungu;
        this.eupmyeondong = eupmyeondong;
        this.current = current;
        this.forecast = forecast;
        this.tomorrow = tomorrow;
        this.dayAfterTomorrow = dayAfterTomorrow;
        this.temperature = temperature;
        this.humidity = humidity;
    }
}
