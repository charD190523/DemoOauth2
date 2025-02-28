package com.example.oauth2test.service;

import com.example.oauth2test.entity.User;
import com.example.oauth2test.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByEmail (String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }
}
