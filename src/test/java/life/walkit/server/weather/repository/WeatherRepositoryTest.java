package life.walkit.server.weather.repository;

import static org.assertj.core.api.Assertions.assertThat;
import life.walkit.server.weather.entity.EupMyeonDong;
import life.walkit.server.weather.entity.Sido;
import life.walkit.server.weather.entity.Sigungu;
import life.walkit.server.weather.entity.Weather;
import life.walkit.server.weather.factory.WeatherTestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
class WeatherRepositoryTest {

    @Autowired
    private WeatherRepository weatherRepository;

    @BeforeEach
    void setUp() {
        weatherRepository.save(WeatherTestFactory.createWeather(EupMyeonDong.JANGHANG1_DONG));
        // 공통 설정이 필요한 경우에만 사용
    }

    @AfterEach
    void tearDown() {
        weatherRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("날씨 저장 성공")
    void save_success() {
        Optional<Weather> found = weatherRepository.findBySidoAndSigunguAndEupmyeondong(
                Sido.GYEONGGI,
                Sigungu.GOYANG_DONGGU,
                EupMyeonDong.JANGHANG1_DONG
        );

        assertThat(found).isPresent()
                .hasValueSatisfying(savedWeather -> {
                    assertThat(savedWeather.getEupmyeondong()).isEqualTo(EupMyeonDong.JANGHANG1_DONG);
                    assertThat(savedWeather.getTemperature()).isEqualTo(27.5);
                });
    }

    @Test
    @DisplayName("주소(시/군/구/읍면동)로 날씨 조회 성공")
    void findByLocation_success() {
        Optional<Weather> found = weatherRepository.findBySidoAndSigunguAndEupmyeondong(
                Sido.GYEONGGI,
                Sigungu.GOYANG_DONGGU,
                EupMyeonDong.JANGHANG1_DONG
        );

        assertThat(found).isPresent()
                .hasValueSatisfying(weather -> {
                    assertThat(weather.getEupmyeondong()).isEqualTo(EupMyeonDong.JANGHANG1_DONG);
                    assertThat(weather.getCurrent()).containsEntry("current", "맑음");});
    }
}
