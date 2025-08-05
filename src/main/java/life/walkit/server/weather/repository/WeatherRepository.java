package life.walkit.server.weather.repository;

import jakarta.persistence.LockModeType;
import life.walkit.server.weather.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

public interface WeatherRepository extends JpaRepository<Weather, Long> {
    Optional<Weather> findByAdminArea(AdminArea adminArea);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Weather w WHERE w.adminArea = :adminArea")
    Optional<Weather> findByAdminAreaWithLock(AdminArea adminArea);
}
