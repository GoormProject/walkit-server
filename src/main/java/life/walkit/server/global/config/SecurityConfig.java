package life.walkit.server.global.config;

import life.walkit.server.auth.filter.JwtAuthenticationFilter;
import life.walkit.server.auth.handler.CustomAuthenticationEntryPoint;
import life.walkit.server.auth.handler.OAuth2LoginSuccessHandler;
import life.walkit.server.auth.resolver.CustomAuthorizationRequestResolver;
import life.walkit.server.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * 인증 없이 접근 가능한 URL 패턴들
     */
    private static final String[] PERMIT_URL_ARRAY = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/actuator/health",
            "/actuator/prometheus",
            "/api/auth/reissue",
            "/login/**"
    };

    private final AuthService authService;
    private final OAuth2LoginSuccessHandler loginSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAuthorizationRequestResolver customResolver;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(
                        session ->
                                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PERMIT_URL_ARRAY).permitAll()
                        .requestMatchers("/api/auth/**").authenticated()
                        .anyRequest().permitAll() // TODO: 기본 인증 요구로 변경
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint) // 토큰 검증 실패시 401 응답
                )
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(
                                // state의 deviceId를 유지하도록 커스터마이징
                                ep ->ep.authorizationRequestResolver(customResolver)
                        )
                        .userInfoEndpoint(user -> user.userService(authService))
                        .successHandler(loginSuccessHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
