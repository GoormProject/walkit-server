package life.walkit.server.auth.service;

import life.walkit.server.member.entity.enums.MemberRole;
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
        OAuth2User user = super.loadUser(userRequest);
        String email = user.getAttribute("email");

        // 임시 사용자 정보 저장용
        Map<String, Object> attributes = new HashMap<>(user.getAttributes());
        attributes.put("email", email);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(MemberRole.USER.name())),
                attributes,
                "email"
        );
    }
}
