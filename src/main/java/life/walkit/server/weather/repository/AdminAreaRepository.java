package life.walkit.server.weather.repository;

import life.walkit.server.weather.entity.AdminArea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminAreaRepository  extends JpaRepository<AdminArea, Long> {
    boolean existsBy();
}
