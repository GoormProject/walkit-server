package life.walkit.server.weather.repository;

import life.walkit.server.weather.entity.EupMyeonDong;
import life.walkit.server.weather.entity.Sido;
import life.walkit.server.weather.entity.Sigungu;
import life.walkit.server.weather.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {
    Optional<Weather> findBySidoAndSigunguAndEupmyeondong(Sido sido, Sigungu sigungu, EupMyeonDong eupMyeonDong);
}
