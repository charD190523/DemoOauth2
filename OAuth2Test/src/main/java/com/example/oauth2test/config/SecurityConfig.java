package com.example.oauth2test.config;

import com.example.oauth2test.enums.Role;
import com.example.oauth2test.handler.AccessDenied;
import com.example.oauth2test.handler.Unauthoized;
import com.example.oauth2test.security.JwtFilter;
import com.example.oauth2test.service.CustomOAuth2UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtFilter jwtFilter;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, JwtFilter jwtFilter) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtFilter = jwtFilter;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   Unauthoized authenticationEntryPoint,
                                                   AccessDenied accessDeniedHandler) throws Exception {
        http
                .authorizeRequests(authorize -> authorize
                        .requestMatchers("/", "/login").permitAll() // Không yêu cầu xác thực
                        .requestMatchers("/users").hasAnyRole(Role.ADMIN.name())
                        .anyRequest().authenticated()          // Các URL khác yêu cầu đăng nhập
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .defaultSuccessUrl("/oauth2/success", true)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint) // Xử lý lỗi 401
                        .accessDeniedHandler(accessDeniedHandler) // Xử lý lỗi 403
                )
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/") // Chuyển hướng sau khi logout
//                        .invalidateHttpSession(true) // Xóa session
//                        .deleteCookies("JSESSIONID") // Xóa cookie phiên đăng nhập
//                )
                .oidcLogout((logout) -> logout
                        .backChannel(Customizer.withDefaults())
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public HttpSessionEventPublisher sessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
