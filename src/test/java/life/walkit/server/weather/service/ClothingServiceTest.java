package life.walkit.server.weather.service;

import life.walkit.server.weather.model.enums.Clouds;
import life.walkit.server.weather.model.enums.Night;
import life.walkit.server.weather.model.enums.Precipitation;
import life.walkit.server.weather.model.enums.Wind;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ClothingServiceTest {

    @Autowired
    private ClothingService clothingService;

    @Test
    @DisplayName("추천 옷차림을 조회합니다.")
    void getClothRecommendations_success() {
        // given
        Wind wind = Wind.CALM;
        Clouds clouds = Clouds.PARTLY;
        Precipitation precip = Precipitation.RAIN;
        Night night = Night.DAYTIME;
        int temp = 31;

        // when
        List<String> cloths = clothingService.getClothRecommendations(wind, clouds, precip, night, temp);

        // then
        assertThat(cloths)
                .containsExactlyInAnyOrder("Cap", "Sunglasses", "Singlet", "Vest", "Shorts", "Sunscreen");
    }
}