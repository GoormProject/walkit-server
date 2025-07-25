package life.walkit.server.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import life.walkit.server.auth.dto.enums.AuthResponse;
import life.walkit.server.auth.entity.enums.JwtTokenType;
import life.walkit.server.auth.jwt.JwtTokenProperties;
import life.walkit.server.auth.service.AuthService;
import life.walkit.server.global.response.BaseResponse;
import life.walkit.server.global.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProperties jwtTokenProperties;

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse> logout(HttpServletResponse response) {

        // 액세스 토큰과 리프레시 토큰을 쿠키에서 제거
        Arrays.stream(JwtTokenType.values()).forEach(tokenType ->
                response.addHeader(
                        HttpHeaders.SET_COOKIE,
                        CookieUtils.deleteCookie(
                                tokenType.name(),
                                true,
                                "None",
                                jwtTokenProperties.domain()
                        ).toString()
                )
        );

        return BaseResponse.toResponseEntity(AuthResponse.LOGOUT_SUCCESS);
    }

    @GetMapping("/me")
    public ResponseEntity<BaseResponse> getCurrentUser(@AuthenticationPrincipal UserDetails member) {
        return BaseResponse.toResponseEntity(
                AuthResponse.GET_CURRENT_MEMBER_SUCCESS,
                authService.getCurrentUser(member.getUsername())
        );
    }
}
