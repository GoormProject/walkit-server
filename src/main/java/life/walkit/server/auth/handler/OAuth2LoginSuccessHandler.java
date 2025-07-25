package life.walkit.server.auth.handler;

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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final JwtTokenIssuer jwtTokenIssuer;
    private final JwtTokenProperties jwtTokenProperties;

    @Value("${app.oauth.login-redirect-url}")
    String loginRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        
        // 회원가입 또는 기존 회원 조회
        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .email(email)
                            .name("")
                            .nickname(email)
                            .status(MemberStatus.OFFLINE)
                            .role(MemberRole.USER)
                            .build();
                    return memberRepository.save(newMember);
                });

        // JWT 토큰 발급
        JwtToken accessToken = jwtTokenIssuer.issueAccessToken(member.getMemberId());
        JwtToken refreshToken = jwtTokenIssuer.issueRefreshToken(member.getMemberId());

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
    }
}
