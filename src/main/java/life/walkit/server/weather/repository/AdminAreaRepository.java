package life.walkit.server.weather.repository;

import life.walkit.server.weather.entity.AdminArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminAreaRepository  extends JpaRepository<AdminArea, Long> {
    Optional<AdminArea> findBySidoAndSigunguAndEupmyeondong(String sido, String sigungu, String eupmyeondong);

    boolean existsBy();
}
