package com.example.oauth2test.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole()));
    }

    @Override
    public String getPassword() {
//        if (user.isOTPRequired()) {
//            return user.getOneTimePassword();
//        }
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
//    public String getEmail() {
//        return user.getEmail();
//    }
}
