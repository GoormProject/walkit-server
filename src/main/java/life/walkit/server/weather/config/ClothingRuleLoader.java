package life.walkit.server.weather.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import life.walkit.server.weather.model.ClothingRule;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Component
public class ClothingRuleLoader {

    private static final String JSON_PATH = "src/main/resources/static/clothing_rules.json";

    private Map<String, List<ClothingRule>> clothingRules = new HashMap<>();

    public static Map<String, List<ClothingRule>> loadRules() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(
                new File(JSON_PATH),
                new TypeReference<Map<String, List<ClothingRule>>>() {}
        );
    }
}
