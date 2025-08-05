package life.walkit.server.weather.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ClothResponse {
    List<String> recommendations;
}
