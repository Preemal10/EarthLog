package com.earthlog.security;

import com.earthlog.entity.User;
import com.earthlog.enums.Role;
import com.earthlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId = attributes.get("sub").toString();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");

        User user = userRepository.findByProviderAndProviderId(provider, providerId)
            .orElseGet(() -> {
                log.info("Creating new user for email: {}", email);
                return userRepository.save(User.builder()
                    .email(email)
                    .name(name)
                    .provider(provider.toUpperCase())
                    .providerId(providerId)
                    .profileImageUrl(picture)
                    .role(Role.USER)
                    .country("DE")
                    .householdSize(1)
                    .build());
            });

        // Update user info if changed
        boolean updated = false;
        if (!user.getName().equals(name)) {
            user.setName(name);
            updated = true;
        }
        if (picture != null && !picture.equals(user.getProfileImageUrl())) {
            user.setProfileImageUrl(picture);
            updated = true;
        }
        if (updated) {
            userRepository.save(user);
        }

        return new CustomOAuth2User(oAuth2User, user);
    }
}
