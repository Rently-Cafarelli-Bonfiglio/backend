package it.polimi.ing_soft.bonfiglio.cafarelli.rently.security;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition.ChatMessageBuilder;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.implementation.ChatMessageBuilderImplementation;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityNotFoundException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationConfigTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationConfig applicationConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        applicationConfig = new ApplicationConfig(userRepository);
    }

    @Test
    void userDetailsService_UserExists_ShouldReturnUser() {
        User mockUser = new User();
        mockUser.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        UserDetailsService service = applicationConfig.userDetailsService();
        User result = (User) service.loadUserByUsername("testuser");

        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void userDetailsService_UserNotFound_ShouldThrowEntityNotFoundException() {
        when(userRepository.findByUsername("notfound"))
                .thenReturn(Optional.empty());

        UserDetailsService service = applicationConfig.userDetailsService();

        assertThrows(EntityNotFoundException.class, () ->
                service.loadUserByUsername("notfound")
        );
    }

    @Test
    void authenticationProvider_ShouldReturnValidProvider() {
        AuthenticationProvider provider = applicationConfig.authenticationProvider();

        assertNotNull(provider);
        assertTrue(provider instanceof DaoAuthenticationProvider);
    }

    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        PasswordEncoder encoder = applicationConfig.passwordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
        assertTrue(encoder.matches("raw", encoder.encode("raw")));
    }

    @Test
    void chatMessageBuilder_ShouldReturnChatMessageBuilderImplementation() {
        ChatMessageBuilder builder = applicationConfig.chatMessageBuilder();

        assertNotNull(builder);
        assertTrue(builder instanceof ChatMessageBuilderImplementation);
    }
}
