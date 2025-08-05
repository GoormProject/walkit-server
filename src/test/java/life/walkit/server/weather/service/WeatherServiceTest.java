package life.walkit.server.weather.service;

import life.walkit.server.weather.dto.WeatherForecastResponseDto;
import life.walkit.server.weather.entity.AdminArea;
import life.walkit.server.weather.entity.Weather;
import life.walkit.server.weather.repository.AdminAreaRepository;
import life.walkit.server.weather.repository.WeatherRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class WeatherServiceTest {

    @Autowired
    private WeatherService weatherService;
    @Autowired
    private WeatherRepository weatherRepository;
    @Autowired
    private AdminAreaRepository adminAreaRepository;

    @Test
    @DisplayName("[경기도_고양시_일산서구_일산1동]의 일기예보를 기상청 API로 조회합니다.")
    void testWeatherFetch_경기도_고양시_일산서구_일산1동() throws Exception {
        AdminArea area = adminAreaRepository.findBySidoAndSigunguAndEupmyeondong(
                "경기도",
                "고양시 일산서구",
                "일산1동"
        ).orElseThrow();

        WeatherForecastResponseDto forecast = weatherService.fetchForecasts(area);

        assertEquals("경기도 고양시 일산서구 일산1동", forecast.getAdminAreaName(), "행정구역 이름이 잘못되었습니다.");
        assertTrue(forecast.getCurrent().getTemperature() > -50, "현재 기온 값이 비정상적입니다.");
        assertNotNull(forecast.getAfter3hours(), "3시간 후 날씨 데이터가 null입니다.");
        assertNotNull(forecast.getTomorrow(), "내일 날씨 데이터가 null입니다.");
        assertNotNull(forecast.getDayAfterTomorrow(), "모레 날씨 데이터가 null입니다.");
        assertNotNull(forecast.getThreeDaysLater(), "3일 후 날씨 데이터가 null입니다.");
    }

    @Test
    @DisplayName("저장된 일기예보를 캐시로 가져옵니다.")
    void testWeatherCache_경기도_고양시_일산서구_일산1동() throws Exception {
        // given
        AdminArea area = adminAreaRepository.findBySidoAndSigunguAndEupmyeondong(
                "경기도",
                "고양시 일산서구",
                "일산1동"
        ).orElseThrow();

        // when - 첫 호출 (캐시에 없어서 외부 API 호출 + 저장)
        WeatherForecastResponseDto first = weatherService.fetchForecasts(area);

        // then - DB에 저장되었는지 확인
        Weather saved = weatherRepository.findByAdminArea(area).orElse(null);
        assertNotNull(saved, "첫 호출 후 날씨가 저장되어 있어야 함");

        // when - 두 번째 호출 (캐시 사용)
        WeatherForecastResponseDto second = weatherService.fetchForecasts(area);

        // then - 저장된 데이터를 그대로 썼는지 확인
        assertEquals(first.getCurrent(), second.getCurrent(), "캐시된 데이터를 재사용해야 함");
    }


}
