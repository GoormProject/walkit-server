package life.walkit.server.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import life.walkit.server.auth.entity.JwtToken;
import life.walkit.server.auth.jwt.JwtTokenIssuer;
import life.walkit.server.auth.jwt.JwtTokenProperties;
import life.walkit.server.global.util.CookieUtils;
import life.walkit.server.member.entity.Member;
import life.walkit.server.member.entity.enums.MemberRole;
import life.walkit.server.member.entity.enums.MemberStatus;
import life.walkit.server.member.repository.MemberRepository;
import life.walkit.server.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberService memberService;
    private final JwtTokenIssuer jwtTokenIssuer;
    private final JwtTokenProperties jwtTokenProperties;

    @Value("${app.oauth.login-redirect-url}")
    String loginRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        try {
            // state 파라미터에서 deviceId 추출
            String deviceId = request.getParameter("state");
            if (deviceId == null || deviceId.isBlank())
                deviceId = "no-device-id";
            // deviceId 길이 제한 (보안상 너무 긴 값 방지)
            if (deviceId.length() > 50)
                deviceId = deviceId.substring(0, 50);

            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            // 회원가입 또는 기존 회원 조회
            Member member = memberService.createMember(
                    oAuth2User.getAttribute("email"),
                    oAuth2User.getAttribute("name"),
                    oAuth2User.getAttribute("profileImage")
            );

            // JWT 토큰 발급
            JwtToken accessToken = jwtTokenIssuer.issueAccessToken(member.getMemberId());
            JwtToken refreshToken = jwtTokenIssuer.issueRefreshToken(member.getMemberId(), deviceId);

            // 쿠키 설정
            response.addHeader(
                    HttpHeaders.SET_COOKIE,
                    CookieUtils.create(
                            accessToken.type().name(),
                            accessToken.value(),
                            accessToken.duration(),
                            true,
                            true,
                            "None",
                            jwtTokenProperties.domain()
                    ).toString()
            );

            response.addHeader(
                    HttpHeaders.SET_COOKIE,
                    CookieUtils.create(
                            refreshToken.type().name(),
                            refreshToken.value(),
                            refreshToken.duration(),
                            true,
                            true,
                            "None",
                            jwtTokenProperties.domain()
                    ).toString()
            );

            response.sendRedirect(loginRedirectUrl);

        } catch (Exception e) {
            log.error("OAuth2 로그인 성공 핸들러 오류 발생", e);
            response.sendRedirect(loginRedirectUrl);
        }
    }
}
