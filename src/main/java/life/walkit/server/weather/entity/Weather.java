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

    @OneToOne
    @JoinColumn(name = "admin_area_id", nullable = false)
    private AdminArea adminArea;

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
    public Weather(AdminArea adminArea, Map<String, Object> current, String forecast, String tomorrow,
                   String dayAfterTomorrow, double temperature, double humidity) {
        this.adminArea = adminArea;
        this.current = current;
        this.forecast = forecast;
        this.tomorrow = tomorrow;
        this.dayAfterTomorrow = dayAfterTomorrow;
        this.temperature = temperature;
        this.humidity = humidity;
    }
}
