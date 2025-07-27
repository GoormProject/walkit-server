package life.walkit.server.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import life.walkit.server.auth.entity.JwtToken;
import life.walkit.server.auth.entity.enums.JwtTokenType;
import life.walkit.server.auth.repository.JwtTokenRepository;
import life.walkit.server.global.util.ClockUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JwtTokenIssuer {

    private final JwtTokenProperties jwtTokenProperties;
    private final JwtTokenRepository jwtTokenRepository;

    public JwtToken issueAccessToken(Long memberId) {
        return JwtToken.of(
                JwtTokenType.ACCESS_TOKEN,
                issueToken(JwtTokenType.ACCESS_TOKEN, memberId, jwtTokenProperties.expiration().access()),
                jwtTokenProperties.accessTokenDuration()
        );
    }

    public JwtToken issueRefreshToken(Long memberId) {
        String refreshToken = issueToken(JwtTokenType.REFRESH_TOKEN, memberId, jwtTokenProperties.expiration().refresh());
        jwtTokenRepository.saveWithTTL(memberId.toString(), refreshToken, jwtTokenProperties.refreshTokenDuration());

        return JwtToken.of(
                JwtTokenType.REFRESH_TOKEN,
                refreshToken,
                jwtTokenProperties.refreshTokenDuration()
        );
    }

    private String issueToken(JwtTokenType type, Long memberId, long expiration) {
        LocalDateTime now = ClockUtils.getLocalDateTime();
        return Jwts.builder()
                .header()
                .add("type", type)
                .and()
                .subject(String.valueOf(memberId))
                .issuedAt(ClockUtils.convertToDate(now))
                .expiration(ClockUtils.getExpirationDate(now, expiration))
                .signWith(Keys.hmacShaKeyFor(jwtTokenProperties.secret().getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
}
