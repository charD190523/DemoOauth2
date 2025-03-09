package com.example.oauth2test.controller;

import com.example.oauth2test.entity.CustomUserDetails;
import com.example.oauth2test.entity.User;
import com.example.oauth2test.security.JwtProvider;
import com.example.oauth2test.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class MainController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @GetMapping("/")
    public String home() {
        return "login";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User user = (OAuth2User) authentication.getPrincipal();

        model.addAttribute("name", user.getAttribute("name"));
        model.addAttribute("email", user.getAttribute("email"));
        model.addAttribute("picture", user.getAttribute("picture"));

        return "userprofile";
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<?> success(Authentication authentication) {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
        }

        String accessToken = user.getAccessToken();

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String jwt = jwtProvider.generateToken(userDetails);
        System.out.println("JWT: " + jwt);

        String redirectUrl = "http://127.0.0.1:5500/home.html?jwt=" + jwt + "&accessToken=" + accessToken;
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUrl)
                .build();
    }


//    @GetMapping("/profile")
//    public String profile(Model model) {
//        // Lấy thông tin người dùng từ SecurityContext
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        OAuth2User user = (OAuth2User) authentication.getPrincipal();
//
//        // Thêm thông tin vào Model
//        model.addAttribute("name", user.getAttribute("name"));
//        model.addAttribute("email", user.getAttribute("email"));
//        model.addAttribute("picture", user.getAttribute("picture"));
//
//        return "userprofile"; // Trả về view userprofile.html
//    }
//
//    @GetMapping("/oauth2/success")
//    public ResponseEntity<?> success(Authentication authentication) {
//        // Lấy OAuth2User từ Authentication
//        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
//
//        // Lấy thông tin từ OAuth2User
//        String email = oauth2User.getAttribute("email");
//
//        // Tìm User trong cơ sở dữ liệu
//        User user = userService.findByEmail(email);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found!");
//        }
//
//        // Tạo JWT token từ CustomUserDetails
//        CustomUserDetails userDetails = new CustomUserDetails(user);
//        String jwt = jwtProvider.generateToken(userDetails);
//        System.out.println("JWT: " + jwt);
//        // Trả về cả Access Token của Google và JWT của hệ thống
//        return ResponseEntity.ok(Map.of(
//                "googleAccessToken", authentication.getCredentials(), // Google Access Token
//                "jwt", jwt // JWT token của hệ thống
//        ));
//    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUser());
    }
}

