package com.example.oauth2test.controller;

import com.example.oauth2test.entity.CustomUserDetails;
import com.example.oauth2test.entity.User;
import com.example.oauth2test.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@CrossOrigin(origins = "http://127.0.0.1:5500", allowCredentials = "true")
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ApiController {

    private final UserService userService;

    @GetMapping("/user")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        Object principal = authentication.getPrincipal();
        String email = null;
        String name = null;
        String picture = null;

        if (principal instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) principal;
            System.out.println("🔹 OAuth2 User Attributes: " + oauth2User.getAttributes());
            email = oauth2User.getAttribute("email");
            name = oauth2User.getAttribute("name");
            picture = oauth2User.getAttribute("picture");
        } else if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            User user = userDetails.getUser();
            email = user.getEmail();
            name = user.getName();
            picture = user.getPicture();
        }

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User information not found.");
        }

        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found in database.");
        }

        return ResponseEntity.ok(Map.of(
                "name", user.getName(),
                "email", user.getEmail(),
                "picture", user.getPicture()
        ));
    }

}
