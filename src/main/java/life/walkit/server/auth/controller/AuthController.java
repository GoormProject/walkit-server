package life.walkit.server.auth.controller;

import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import life.walkit.server.auth.dto.CurrentUserDto;
import life.walkit.server.auth.dto.CustomMemberDetails;
import life.walkit.server.auth.dto.enums.AuthResponse;
import life.walkit.server.auth.entity.JwtToken;
import life.walkit.server.auth.entity.enums.JwtTokenType;
import life.walkit.server.auth.error.JwtTokenException;
import life.walkit.server.auth.error.enums.JwtTokenErrorCode;
import life.walkit.server.auth.jwt.JwtTokenIssuer;
import life.walkit.server.auth.jwt.JwtTokenParser;
import life.walkit.server.auth.jwt.JwtTokenProperties;
import life.walkit.server.auth.repository.JwtTokenRepository;
import life.walkit.server.auth.service.AuthService;
import life.walkit.server.global.response.BaseResponse;
import life.walkit.server.global.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Tag(name = "인증", description = "인증 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProperties jwtTokenProperties;
    private final JwtTokenRepository jwtTokenRepository;
    private final JwtTokenIssuer jwtTokenIssuer;
    private final JwtTokenParser jwtTokenParser;

    @Operation(summary = "로그아웃", description = "AccessToken 및 RefreshToken 쿠키를 삭제하여 로그아웃합니다.")
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(
            @AuthenticationPrincipal CustomMemberDetails member,
            @RequestParam String deviceId, HttpServletResponse response
    ) {

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
        
        // Redis에서 리프레시 토큰 제거
        CurrentUserDto currentUser = authService.getCurrentUser(member.getUsername());
        String key = currentUser.getMemberId() + ":" + deviceId;
        jwtTokenRepository.deleteByKey(key);

        return BaseResponse.toResponseEntity(AuthResponse.LOGOUT_SUCCESS);
    }

    @Operation(summary = "액세스 토큰 재발급", description = "리프레시 토큰을 이용해 액세스 토큰을 재발급합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<BaseResponse<Void>> reissueAccessToken(
            @RequestParam String deviceId,
            HttpServletRequest request, HttpServletResponse response
    ) {

        String refreshToken = jwtTokenParser.resolveRefreshToken(request);
        if (refreshToken == null || refreshToken.isBlank())
            throw new JwtTokenException(JwtTokenErrorCode.REFRESH_TOKEN_NOT_FOUND);

        try {
            // JWT 토큰 검증 및 Claims 파싱
            Claims claims = jwtTokenParser.parseClaims(refreshToken);
            String subject = claims.getSubject();

            if (subject == null || subject.trim().isEmpty())
                throw new JwtTokenException(JwtTokenErrorCode.REFRESH_TOKEN_NOT_FOUND);

            Long memberId = Long.parseLong(subject);
            String key = memberId + ":" + deviceId;

            // Redis에 저장된 리프레시 토큰 조회
            String redisRefreshToken = jwtTokenRepository.findByKey(key)
                    .orElseThrow(() -> new JwtTokenException(JwtTokenErrorCode.REFRESH_TOKEN_INVALID));

            // 저장된 리프레시 토큰과 일치하면 액세스 토큰 재발급
            if (refreshToken.equals(redisRefreshToken)) {
                JwtToken accessToken = jwtTokenIssuer.issueAccessToken(memberId);

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
            } else {
                throw new JwtTokenException(JwtTokenErrorCode.REFRESH_TOKEN_INVALID);
            }
        } catch (Exception e) {
            throw new JwtTokenException(JwtTokenErrorCode.REFRESH_TOKEN_INVALID);
        }

        return BaseResponse.toResponseEntity(AuthResponse.ACCESS_TOKEN_REISSUE_SUCCESS);
    }

    @Operation(summary = "본인확인", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<CurrentUserDto>> getCurrentUser(
            @AuthenticationPrincipal CustomMemberDetails member
    ) {
        return BaseResponse.toResponseEntity(
                AuthResponse.GET_CURRENT_MEMBER_SUCCESS,
                authService.getCurrentUser(member.getUsername())
        );
    }
}
