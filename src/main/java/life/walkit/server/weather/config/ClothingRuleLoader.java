package life.walkit.server.weather.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import life.walkit.server.weather.model.ClothingRule;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Component
public class ClothingRuleLoader {

    private static final String JSON_PATH = "/static/clothing_rules.json";

    private Map<String, List<ClothingRule>> clothingRules = new HashMap<>();

    public static Map<String, List<ClothingRule>> loadRules() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = ClothingRuleLoader.class.getResourceAsStream(JSON_PATH)) {
            if (inputStream == null) {
                throw new IOException("Cannot find resource: " + JSON_PATH);
            }
            return mapper.readValue(inputStream, new TypeReference<Map<String, List<ClothingRule>>>() {});
        }
    }
}
