package life.walkit.server.weather.entity;

import jakarta.persistence.*;
import life.walkit.server.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
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
    private Map<String, Object> forecast;

    @Column(name = "tomorrow", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> tomorrow;

    @Column(name = "dayAfterTomorrow", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> dayAfterTomorrow;

    @Column(name = "threeDaysLater", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> threeDaysLater;

    @Column(name = "temperature", nullable = false)
    private double temperature;

    @Column(name = "humidity", nullable = false)
    private double humidity;

    @Builder
    public Weather(AdminArea adminArea, Map<String, Object> current, Map<String, Object> forecast,
                   Map<String, Object> tomorrow, Map<String, Object> dayAfterTomorrow,
                   Map<String, Object> threeDaysLater, double temperature, double humidity) {
        this.adminArea = adminArea;
        this.current = current;
        this.forecast = forecast;
        this.tomorrow = tomorrow;
        this.dayAfterTomorrow = dayAfterTomorrow;
        this.threeDaysLater = threeDaysLater;
        this.temperature = temperature;
        this.humidity = humidity;
    }
    
    public boolean isStale(LocalDateTime now) {
        // 마지막 수정으로 부터 1시간 경과했는지 확인
        return getModifiedAt() == null || getModifiedAt().isBefore(now.minusHours(1));
    }
}
