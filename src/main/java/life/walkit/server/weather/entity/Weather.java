package life.walkit.server.weather.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "weather")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Weather {

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
    private String current;

    @Column(name = "forecast", columnDefinition = "jsonb", nullable = false)
    private String forecast;

    @Column(name = "tomorrow", columnDefinition = "jsonb", nullable = false)
    private String tomorrow;

    @Column(name = "dayAfterTomorrow", columnDefinition = "jsonb", nullable = false)
    private String dayAfterTomorrow;

    @Column(name = "temperature", nullable = false)
    private double temperature;

    @Column(name = "humidity", nullable = false)
    private double humidity;

    @Builder
    public Weather(Sido sido, Sigungu sigungu, EupMyeonDong eupmyeondong,
                   String current, String forecast, String tomorrow, String dayAfterTomorrow, double temperature, double humidity) {
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
