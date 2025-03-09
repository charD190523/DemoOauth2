package com.example.oauth2test.service;

import com.example.oauth2test.entity.CustomUserDetails;
import com.example.oauth2test.entity.User;
import com.example.oauth2test.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String accessToken = userRequest.getAccessToken().getTokenValue();
        System.out.println("🔹 Access Token: " + accessToken);

        String email = oAuth2User.getAttribute("email");

        Optional<User> optionalUser = userRepository.findByEmail(email);

        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            user.setAccessToken(accessToken);
            System.out.println("🔄 Updating Access Token for existing user: " + email);
        } else {
            // Nếu user chưa tồn tại, tạo user mới
            user = new User();
            user.setEmail(email);
            user.setName(oAuth2User.getAttribute("name"));
            user.setPicture(oAuth2User.getAttribute("picture"));
            user.setProvider(userRequest.getClientRegistration().getRegistrationId());
            user.setAccessToken(accessToken);
            user.setRole("ROLE_USER"); // Đặt role mặc định
            System.out.println("🆕 Creating new user with Access Token: " + email);
        }

        // Lưu user vào DB (nếu có thay đổi)
        userRepository.save(user);

        return oAuth2User;
    }


    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        return new CustomUserDetails(user);
    }



    public UserDetails loadUserById(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return this.loadUserByUsername(user.getEmail());
    }
}
