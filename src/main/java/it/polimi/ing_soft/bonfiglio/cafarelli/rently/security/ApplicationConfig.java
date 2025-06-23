package it.polimi.ing_soft.bonfiglio.cafarelli.rently.security;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition.ChatMessageBuilder;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.implementation.ChatMessageBuilderImplementation;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityNotFoundException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for application security and authentication.
 * It defines beans for user details service, authentication provider, and password encoder.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepository userRepository;

    /**
     * Bean that provides user details service for authentication.
     * It retrieves user details from the UserRepository based on the username.
     *
     * @return An instance of {@link UserDetailsService}.
     */

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(User.class));
    }

    /**
     * Bean that specifies how to manage authentication.
     *
     * @return An instance of {@link AuthenticationProvider}.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /**
     * Bean that specifies how to encode passwords.
     *
     * @return An instance of {@link PasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean that provides a chat message builder for creating chat messages.
     *
     * @return An instance of {@link ChatMessageBuilder}.
     */

    @Bean
    public ChatMessageBuilder chatMessageBuilder() {
        return new ChatMessageBuilderImplementation();
    }
}
