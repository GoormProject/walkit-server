package life.walkit.server.weather.service;

import life.walkit.server.weather.dto.WeatherResponseDto;
import life.walkit.server.weather.entity.AdminArea;
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
    void testWeatherFetch_경기도_고양시_일산서구_일산1동() throws Exception {
        // given
        String nx = "60";
        String ny = "126";
        AdminArea adminArea = AdminArea.builder()
                .sido("경기도")
                .sigungu("고양시 일산서구")
                .eupmyeondong("일산1동")
                .x(Integer.parseInt(nx))
                .y(Integer.parseInt(ny))
                .build();

        // when
        WeatherResponseDto dto = weatherService.fetchWeather(nx, ny, adminArea);

        // then
        assertNotNull(dto);
        assertEquals(adminArea.getSido() + " " + adminArea.getSigungu() + " " + adminArea.getEupmyeondong(), dto.getAdminAreaName());
        assertTrue(dto.getTemperature() > -50); // 대기온도 sanity check
    }

}
