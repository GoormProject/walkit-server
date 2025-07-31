package life.walkit.server.weather.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import life.walkit.server.weather.dto.AdminAreaDto;
import life.walkit.server.weather.repository.AdminAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminAreaInitializer  implements CommandLineRunner {

    private static final String JSON_PATH = "/static/admin_area.json";

    private final AdminAreaRepository adminAreaRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        if (adminAreaRepository.existsAny()) {
            return;
        }

        try (InputStream inputStream = AdminAreaInitializer.class.getResourceAsStream(JSON_PATH)) {
            if (inputStream == null) {
                throw new IOException("Cannot find resource: " + JSON_PATH);
            }
            List<AdminAreaDto> areas = objectMapper.readValue(inputStream, new TypeReference<>() {});
            adminAreaRepository.saveAll(
                    areas.stream()
                            .map(AdminAreaDto::toEntity)
                            .toList()
            );
        }
    }
}
