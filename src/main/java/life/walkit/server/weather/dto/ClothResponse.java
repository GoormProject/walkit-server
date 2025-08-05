package life.walkit.server.weather.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ClothResponse {

    @ArraySchema(
            schema = @Schema(
                    description = """
                추천 가능한 옷 목록입니다. 아래 항목 중 상황에 맞는 옷들이 추천됩니다.

                - Cap: 머리 (얇은 모자)
                - Beanie: 머리 (따뜻한 모자)
                - Singlet: 상체 가장 안쪽
                - T-Shirt: 상체 안쪽
                - Long Sleeve Shirt: 상체 중간
                - Vest: 상체 겉
                - Jacket: 상체 겉 (방풍/보온)
                - Heavy Jacket: 상체 가장 바깥
                - Gloves: 손 (기본)
                - Gloves or Mittens: 손 (추위용)
                - Tights: 다리 (안쪽)
                - Pants: 다리 (기본)
                - Shorts: 다리 (겉옷)
                - Sunscreen: 피부 보호 (노출 부위)
                - Sunglasses: 눈 보호
                """,
                    example = "[\"Cap\", \"Sunglasses\", \"Singlet\", \"Shorts\", \"Sunscreen\"]"
            )
    )
    List<String> recommendations;
}
