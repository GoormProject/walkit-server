package life.walkit.server.auth.service;

import life.walkit.server.auth.dto.CurrentUserDto;
import life.walkit.server.auth.error.OAuthException;
import life.walkit.server.auth.error.enums.OAuthErrorCode;
import life.walkit.server.member.entity.Member;
import life.walkit.server.member.entity.enums.MemberRole;
import life.walkit.server.member.error.MemberException;
import life.walkit.server.member.error.enums.MemberErrorCode;
import life.walkit.server.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "google", "kakao" 등
        OAuth2User user = super.loadUser(userRequest);

        // 전달할 사용자 정보 저장
        Map<String, Object> attributes = new HashMap<>();
        if ("google".equals(registrationId)) {
            attributes = getAttributesOfGoogleLogin(user);
        }
        else if ("kakao".equals(registrationId)) {
            attributes = getAttributesOfKakaoLogin(user);
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(MemberRole.USER.name())),
                attributes,
                "email"
        );
    }

    public CurrentUserDto getCurrentUser(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND)
        );

        return CurrentUserDto.of(member);
    }

    private Map<String, Object> getAttributesOfGoogleLogin(OAuth2User user) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", user.getAttribute("email"));
        attributes.put("name", user.getAttribute("name"));
        attributes.put("profileImage", user.getAttribute("picture"));
        return attributes;
    }

    private Map<String, Object> getAttributesOfKakaoLogin(OAuth2User user) {
        Map<String, Object> attributes = new HashMap<>();

        Map<String, Object> kakaoAccount = user.getAttribute("kakao_account");
        if (kakaoAccount == null) {
            throw new OAuthException(OAuthErrorCode.KAKAO_OAUTH_ACCOUNT_DATA_MISSING);
        }

        String email = (String) kakaoAccount.get("email"); // 필수
        attributes.put("email", email);

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String name = profile != null ? (String) profile.get("nickname") : "";
        String profileImage = profile != null ? (String) profile.get("profile_image_url") : "";

        attributes.put("name", name != null ? name : "");
        attributes.put("profileImage", profileImage != null ? profileImage : "");

        return attributes;
    }
}
