package life.walkit.server.auth.controller;

import life.walkit.server.auth.dto.enums.AuthResponse;
import life.walkit.server.auth.service.AuthService;
import life.walkit.server.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<BaseResponse> getCurrentUser(@AuthenticationPrincipal UserDetails member) {
        return BaseResponse.toResponseEntity(
                AuthResponse.GET_CURRENT_MEMBER_SUCCESS,
                authService.getCurrentUser(member.getUsername())
        );
    }
}
