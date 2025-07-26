package life.walkit.server.global.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;

public class CookieUtils {

    public static ResponseCookie create(String name, String value, Duration maxAge, boolean httpOnly, boolean secure, String sameSite, String domain) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, URLEncoder.encode(value, StandardCharsets.UTF_8))
                .httpOnly(httpOnly)
                .secure(secure)
                .path("/")
                .maxAge(maxAge)
                .sameSite(sameSite);

        if (domain != null && !domain.isEmpty()) {
            builder.domain(domain);
        }

        return builder.build();
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .map(cookie -> URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8))
                .findFirst()
                .orElse(null);
    }

    public static ResponseCookie deleteCookie(String name, boolean secure, String sameSite, String domain) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, "")
                .path("/")
                .httpOnly(true)
                .secure(secure)
                .maxAge(0)
                .sameSite(sameSite);

        if (domain != null && !domain.isEmpty()) {
            builder.domain(domain);
        }

        return builder.build();
    }

}
