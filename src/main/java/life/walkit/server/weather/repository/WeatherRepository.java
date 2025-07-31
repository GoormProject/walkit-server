package life.walkit.server.weather.repository;

import life.walkit.server.weather.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {
    Optional<Weather> findByAdminArea(AdminArea adminArea);
}
