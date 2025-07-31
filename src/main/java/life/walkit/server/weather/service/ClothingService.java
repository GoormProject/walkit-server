package life.walkit.server.weather.service;

import life.walkit.server.weather.config.ClothingRuleLoader;
import life.walkit.server.weather.model.ClothingRule;
import life.walkit.server.weather.model.enums.Clouds;
import life.walkit.server.weather.model.enums.Night;
import life.walkit.server.weather.model.enums.Precipitation;
import life.walkit.server.weather.model.enums.Wind;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ClothingService {

    private final Map<String, List<ClothingRule>> clothingRules;

    public ClothingService() {
        try {
            clothingRules = ClothingRuleLoader.loadRules();
        } catch (IOException e) {
            log.error("Failed to load clothing rules", e);
            throw new RuntimeException("Failed to initialize ClothingService", e);
        }
    }

    public List<String> getClothRecommendations(Wind wind, Clouds clouds, Precipitation precip, Night night, int temp) {
        List<String> recommendations = new ArrayList<>();

        for (String cloth : clothingRules.keySet()) {
            for (ClothingRule rule : clothingRules.get(cloth)) {
                if (rule.matches(wind, clouds, precip, night, temp)) {
                    recommendations.add(cloth);
                    break;
                }
            }
        }

        return recommendations;
    }
}
