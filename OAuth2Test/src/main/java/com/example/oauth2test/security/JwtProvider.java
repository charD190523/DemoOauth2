package com.example.oauth2test.security;

import com.example.oauth2test.entity.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    // Tạo một SecretKey bảo mật thay vì sử dụng chuỗi bí mật
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    /**
     * Tạo JWT token cho người dùng.
     */
    public String generateToken(CustomUserDetails userDetails) {
        Date now = new Date();
        // 2 ngày (2 * 24 * 60 * 60 * 1000)
        long JWT_EXPIRATION = 172800000L;
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);
        return Jwts.builder()
                .setSubject(Long.toString(userDetails.getUser().getId()))
                .claim("roles", userDetails.getAuthorities().stream()
                        .toList()) // Chuyển danh sách quyền thành String
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();

    }

    /**
     * Lấy user ID từ JWT token.
     */
    public Integer getUserIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Integer.parseInt(claims.getSubject());
    }

    /**
     * Kiểm tra tính hợp lệ của JWT token.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }
}
