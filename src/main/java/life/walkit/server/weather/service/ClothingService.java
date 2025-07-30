package life.walkit.server.weather.service;

import life.walkit.server.weather.config.ClothingRuleLoader;
import life.walkit.server.weather.model.ClothingRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClothingService {

    private final static Map<String, List<ClothingRule>> clothingRules;

    static {
        try {
            clothingRules = ClothingRuleLoader.loadRules();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
