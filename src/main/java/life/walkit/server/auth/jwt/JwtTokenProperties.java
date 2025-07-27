package life.walkit.server.auth.jwt;

import jakarta.annotation.PostConstruct;
import life.walkit.server.auth.error.JwtTokenException;
import life.walkit.server.auth.error.enums.JwtTokenErrorCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@ConfigurationProperties(prefix = "jwt")
public record JwtTokenProperties (
        String secret,
        Expiration expiration,
        String domain
) {

    @PostConstruct
    public void validate() {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new JwtTokenException(JwtTokenErrorCode.TOKEN_SECRET_KEY_INVALID_LENGTH);
        }
    }

    public record Expiration(long access, long refresh) { }

    public Duration accessTokenDuration() {
        return Duration.ofSeconds(expiration.access());
    }

    public Duration refreshTokenDuration() {
        return Duration.ofSeconds(expiration.refresh());
    }
}
