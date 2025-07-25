package life.walkit.server.auth.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import life.walkit.server.auth.dto.CustomMemberDetails;
import life.walkit.server.auth.jwt.JwtTokenParser;
import life.walkit.server.member.entity.Member;
import life.walkit.server.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenParser jwtTokenParser;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. 쿠키에서 JWT 토큰 추출
            String token = jwtTokenParser.resolveToken(request);

            // 2. 토큰이 존재하고 유효한 경우에만 처리
            if (token != null && !token.trim().isEmpty()) {

                // 3. JWT 토큰 검증 및 Claims 파싱
                Claims claims = jwtTokenParser.parseClaims(token);
                String subject = claims.getSubject();
                
                // subject가 null이거나 빈 문자열인 경우 처리
                if (subject == null || subject.trim().isEmpty()) {
                    sendUnauthorizedResponse(response, "유효하지 않은 토큰입니다.");
                    return;
                }
                
                Long memberId = Long.parseLong(subject);
                
                // 4. memberId로 사용자 정보 조회
                Member member = memberRepository.findById(memberId)
                        .orElse(null);

                // 5. 유효한 사용자인 경우 SecurityContextHolder에 인증 정보 설정
                if (member != null) {
                    CustomMemberDetails userDetails = new CustomMemberDetails(member);
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // 사용자를 찾을 수 없는 경우 401 반환
                    sendUnauthorizedResponse(response, "유효하지 않은 사용자입니다.");
                    return;
                }
            } else {
                log.debug("쿠키에 JWT 토큰이 없습니다.");
            }
        } catch (ExpiredJwtException e) {
            log.info("JWT 토큰이 만료되었습니다: {}", e.getMessage());
            sendUnauthorizedResponse(response, "토큰이 만료되었습니다.");
            return;
        } catch (UnsupportedJwtException e) {
            log.info("지원하지 않는 JWT 토큰입니다: {}", e.getMessage());
            sendUnauthorizedResponse(response, "지원하지 않는 토큰 형식입니다.");
            return;
        } catch (MalformedJwtException e) {
            log.info("잘못된 JWT 토큰 형식입니다: {}", e.getMessage());
            sendUnauthorizedResponse(response, "잘못된 토큰 형식입니다.");
            return;
        } catch (SignatureException e) {
            log.info("JWT 토큰 서명이 유효하지 않습니다: {}", e.getMessage());
            sendUnauthorizedResponse(response, "토큰 서명이 유효하지 않습니다.");
            return;
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 비어있습니다: {}", e.getMessage());
            sendUnauthorizedResponse(response, "토큰이 비어있습니다.");
            return;
        } catch (Exception e) {
            log.info("JWT 토큰 처리 중 예상치 못한 오류 발생: {}", e.getMessage());
            sendUnauthorizedResponse(response, "토큰 처리 중 오류가 발생했습니다.");
            return;
        }
        
        // 6. 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        // 애플리케이션의 BaseResponse 형식과 일치하는 JSON 응답
        String jsonResponse = String.format("""
            {
              "httpStatus": 401,
              "message": "%s",
              "data": null
            }
            """, message);
        
        response.getWriter().write(jsonResponse);
    }
} 
