package life.walkit.server.weather.service;

import life.walkit.server.weather.dto.WeatherForecastResponseDto;
import life.walkit.server.weather.entity.AdminArea;
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

    @Test
    @DisplayName("[경기도_고양시_일산서구_일산1동]의 일기예보를 기상청 API로 조회합니다.")
    void testWeatherFetch_경기도_고양시_일산서구_일산1동() throws Exception {
        String nx = "60";
        String ny = "126";
        AdminArea adminArea = AdminArea.builder()
                .sido("경기도")
                .sigungu("고양시 일산서구")
                .eupmyeondong("일산1동")
                .x(Integer.parseInt(nx))
                .y(Integer.parseInt(ny))
                .build();

        WeatherForecastResponseDto forecast = weatherService.fetchForecasts(adminArea).block();

        assertEquals("경기도 고양시 일산서구 일산1동", forecast.getAdminAreaName(), "행정구역 이름이 잘못되었습니다.");
        assertTrue(forecast.getCurrent().getTemperature() > -50, "현재 기온 값이 비정상적입니다.");
        assertNotNull(forecast.getAfter3hours(), "3시간 후 날씨 데이터가 null입니다.");
        assertNotNull(forecast.getTomorrow(), "내일 날씨 데이터가 null입니다.");
        assertNotNull(forecast.getDayAfterTomorrow(), "모레 날씨 데이터가 null입니다.");
        assertNotNull(forecast.getThreeDaysLater(), "3일 후 날씨 데이터가 null입니다.");
    }



}
