package life.walkit.server.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import life.walkit.server.auth.entity.enums.JwtTokenType;
import life.walkit.server.auth.error.JwtTokenException;
import life.walkit.server.auth.error.enums.JwtTokenErrorCode;
import life.walkit.server.global.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class JwtTokenParser {

    private final JwtTokenProperties jwtTokenProperties;

    public Claims parseClaims(String token) {
        return getJwtParser()
                .parseSignedClaims(token)
                .getPayload();
    }

    private JwtParser getJwtParser() {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtTokenProperties.secret().getBytes(StandardCharsets.UTF_8)))
                .build();
    }

    public String resolveToken(HttpServletRequest request) {
        return CookieUtils.getCookieValue(request, JwtTokenType.ACCESS_TOKEN.name());
    }

}
