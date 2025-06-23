package it.polimi.ing_soft.bonfiglio.cafarelli.rently.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Service class for handling JSON Web Tokens (JWT) operations.
 *
 * <p>This class provides methods to generate, validate, and extract claims from JWTs.
 * It uses a secret key for signing and verifying the tokens.</p>
 */

@Service
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    /**
     * Retrives the signing key used to generate JSON Web Tokens (JWTs).
     *
     * @return the signing key
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    /**
     * Extracts all claims from a given JSON Web Token (JWT).
     *
     * <p>This method parses the JWT using a secret key to verify its signature and
     * retrieves the payload containing claims. Claims represent key-value pairs
     * that provide information about the token's subject, such as user details, roles,
     * and expiration time.</p>
     *
     * @param token the JWT from which to extract claims
     * @return a {@link Claims} object containing all claims extracted from the token
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extracts a specific claim from a given JSON Web Token (JWT).
     *
     * <p>This method parses the JWT using a secret key to verify its signature and
     * retrieves the payload containing claims. Claims represent key-value pairs
     * that provide information about the token's subject, such as user details, roles,
     * and expiration time.</p>
     *
     * @param token the JWT from which to extract the claim
     * @param claimsResolver a function that extracts the claim value from the claims object
     * @param <T> the type of the claim value
     * @return the value of the claim extracted from the token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts the username from a given JSON Web Token (JWT).
     *
     * @param token the JWT from which to extract the username
     * @return the username extracted from the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from a given JSON Web Token (JWT).
     *
     * @param token the JWT from which to extract the expiration date
     * @return the expiration date extracted from the token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Verifies whether a given JSON Web Token (JWT) is expired.
     *
     * @param token the JWT to verify
     * @return {@code true} if the token is expired, {@code false} otherwise
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Checks whether a given JSON Web Token (JWT) is valid for a given user.
     *
     * @param token the JWT to validate
     * @param userDetails the user to validate the token against
     * @return  {@code true} if the token is valid for the user, {@code false} otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Generates a JSON Web Token (JWT) for a given user.
     *
     * <p>This method creates a JWT using a secret key to sign the token and
     * includes the user's username as the subject, the current date as the issue date,
     * and the expiration date calculated based on the JWT expiration time.</p>
     *
     * @param extraClaims additional claims to include in the JWT payload
     *                    (e.g., user roles, permissions, etc.)
     * @param userDetails the user for which to generate the JWT
     * @return the generated JWT
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generates a JSON Web Token (JWT) for a given user.
     *
     * <p>This method creates a JWT using a secret key to sign the token and
     * includes the user's username as the subject, the current date as the issue date,
     * and the expiration date calculated based on the JWT expiration time.</p>
     *
     * @param userDetails the user for which to generate the JWT
     * @return the generated JWT
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(Collections.emptyMap(), userDetails);
    }
}