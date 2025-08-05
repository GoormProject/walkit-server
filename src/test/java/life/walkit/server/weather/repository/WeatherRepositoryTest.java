package life.walkit.server.weather.repository;

import static org.assertj.core.api.Assertions.assertThat;

import life.walkit.server.weather.entity.*;
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
    @Autowired
    private AdminAreaRepository adminAreaRepository;

    @BeforeEach
    void setUp() {
        AdminArea area = adminAreaRepository.findBySidoAndSigunguAndEupmyeondong(
                "경기도",
                "고양시 일산동구",
                "장항1동"
        ).orElseThrow();
        weatherRepository.save(WeatherTestFactory.createWeather(area));
        // 공통 설정이 필요한 경우에만 사용
    }

    @AfterEach
    void tearDown() {
        weatherRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("날씨 저장 성공")
    void save_success() {
        AdminArea area = adminAreaRepository.findBySidoAndSigunguAndEupmyeondong(
                "경기도",
                "고양시 일산동구",
                "장항1동"
        ).orElseThrow();

        Optional<Weather> found = weatherRepository.findByAdminArea(area);

        assertThat(found).isPresent()
                .hasValueSatisfying(savedWeather -> {
                    assertThat(savedWeather.getAdminArea().getEupmyeondong()).isEqualTo(area.getEupmyeondong());
                    assertThat(savedWeather.getTemperature()).isEqualTo(27.5);
                });
    }

    @Test
    @DisplayName("행정 구역(시/군/구/읍면동)으로 날씨 조회 성공")
    void findByLocation_success() {
        AdminArea area = adminAreaRepository.findBySidoAndSigunguAndEupmyeondong(
                "경기도",
                "고양시 일산동구",
                "장항1동"
        ).orElseThrow();

        Optional<Weather> found = weatherRepository.findByAdminArea(area);

        assertThat(found).isPresent()
                .hasValueSatisfying(weather -> {
                    assertThat(weather.getAdminArea().getEupmyeondong()).isEqualTo(area.getEupmyeondong());
                    assertThat(weather.getCurrent()).containsEntry("weather", "맑음");});
    }
}
