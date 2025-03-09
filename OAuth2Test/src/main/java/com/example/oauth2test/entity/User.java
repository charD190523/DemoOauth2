package com.example.oauth2test.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    private String name;

    @Column(nullable = false)
    private String role;

    private String provider; // OAuth2 provider (Google, Facebook, etc.)

    @Column(columnDefinition = "TEXT")
    private String picture; // URL ảnh đại diện (profile picture)

    private String accessToken;

}
