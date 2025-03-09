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
                        .requestMatchers("/", "/login").permitAll()
                        .requestMatchers("/users").hasAnyRole(Role.ADMIN.name())
                        .requestMatchers("/api/user").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .defaultSuccessUrl("/oauth2/success",  true)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("http://127.0.0.1:5500/login.html")
                        .invalidateHttpSession(true) // Xóa session
                        .deleteCookies("JSESSIONID")
                )
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
