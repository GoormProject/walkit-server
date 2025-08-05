package life.walkit.server.weather.repository;

import life.walkit.server.weather.entity.AdminArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdminAreaRepository  extends JpaRepository<AdminArea, Long> {
    Optional<AdminArea> findBySidoAndSigunguAndEupmyeondong(String sido, String sigungu, String eupmyeondong);

    @Query("SELECT COUNT(a) > 0 FROM AdminArea a")
    boolean existsAny();

    @Query(value = """
    SELECT *
    FROM admin_area
    ORDER BY location <-> ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
    LIMIT 1
    """, nativeQuery = true)
    AdminArea findNearestArea(@Param("lat") double lat, @Param("lng") double lng);
}
