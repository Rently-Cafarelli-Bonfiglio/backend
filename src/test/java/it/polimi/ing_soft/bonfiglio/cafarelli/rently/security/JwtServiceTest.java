package it.polimi.ing_soft.bonfiglio.cafarelli.rently.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private String secretKey = "ZmFrZV9rZXlfZmFrZV9rZXlfZmFrZV9rZXlfZmFrZV9rZXlfZmFrZV9rZXk="; // Base64 of 32+ chars
    private long expiration = 1000 * 60 * 60; // 1 hour

    private UserDetails userDetails;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();

        // Usa reflection per impostare i campi privati
        Field secretKeyField = JwtService.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKeyField.set(jwtService, secretKey);

        Field jwtExpirationField = JwtService.class.getDeclaredField("jwtExpiration");
        jwtExpirationField.setAccessible(true);
        jwtExpirationField.set(jwtService, expiration);

        Field refreshTokenExpirationField = JwtService.class.getDeclaredField("refreshTokenExpiration");
        refreshTokenExpirationField.setAccessible(true);
        refreshTokenExpirationField.set(jwtService, expiration * 2);

        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities("USER")
                .build();
    }
    @Test
    void generateToken_ShouldCreateValidToken() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("testuser", extractedUsername);
    }

    @Test
    void extractAllClaims_ShouldReturnClaims() {
        String token = jwtService.generateToken(userDetails);
        Claims claims = jwtService.extractAllClaims(token);

        assertEquals("testuser", claims.getSubject());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void isTokenValid_ShouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenExpired_ShouldReturnFalseForFreshToken() {
        String token = jwtService.generateToken(userDetails);
        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void extractExpiration_ShouldReturnFutureDate() {
        String token = jwtService.generateToken(userDetails);
        Date expiration = jwtService.extractExpiration(token);

        assertTrue(expiration.after(new Date()));
    }

    @Test
    void extractClaim_ShouldReturnSubjectUsingResolver() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractClaim(token, Claims::getSubject);

        assertEquals("testuser", username);
    }

    @Test
    void generateToken_WithExtraClaims_ShouldIncludeClaims() {
        Map<String, Object> claims = Map.of("role", "ADMIN");
        String token = jwtService.generateToken(claims, userDetails);

        Claims extractedClaims = jwtService.extractAllClaims(token);
        assertEquals("ADMIN", extractedClaims.get("role"));
    }
}
