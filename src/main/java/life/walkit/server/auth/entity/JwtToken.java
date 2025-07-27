package life.walkit.server.auth.entity;

import life.walkit.server.auth.entity.enums.JwtTokenType;
import lombok.AccessLevel;
import lombok.Builder;

import java.time.Duration;

@Builder(access = AccessLevel.PRIVATE)
public record JwtToken(
        JwtTokenType type,
        String value,
        Duration duration
) {
    public static JwtToken of(JwtTokenType type, String value, Duration duration) {
        return JwtToken.builder()
                .type(type)
                .value(value)
                .duration(duration)
                .build();
    }
}
