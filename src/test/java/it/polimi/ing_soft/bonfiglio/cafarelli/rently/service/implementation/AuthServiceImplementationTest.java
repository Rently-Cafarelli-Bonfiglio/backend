package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.UserLoginRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.UserRegistrationRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.LoginResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityNotFoundException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityRegistrationException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.UserDisabledException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Role;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.UserRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplementationTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private LocalStorageService localStorageService;

    @InjectMocks private AuthServiceImplementation authService;

    private User user;
    private UserRegistrationRequest registrationRequest;
    private UserLoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test user
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setFirstname("Test");
        user.setLastname("User");
        user.setRole(Role.CLIENT);
        user.setActive(true);
        user.setBalance(BigDecimal.ZERO);
        user.setImageUrl("default.jpg");

        // Setup registration request
        registrationRequest = new UserRegistrationRequest();
        registrationRequest.setUsername("newuser");
        registrationRequest.setEmail("new@example.com");
        registrationRequest.setPassword("password123");
        registrationRequest.setFirstname("New");
        registrationRequest.setLastname("User");

        // Setup login request
        loginRequest = new UserLoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        // Default mocks
        when(localStorageService.getDefaultUserPhoto()).thenReturn("default.jpg");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    }

    @Test
    void registerUser_success() {
        // Arrange
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());

        // Act
        CustomResponse response = authService.registerUser(registrationRequest, null);

        // Assert
        assertEquals("User registered successfully", response.getMessage());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_withImage_success() {
        // Arrange
        MultipartFile mockImage = mock(MultipartFile.class);
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
        when(mockImage.isEmpty()).thenReturn(false);
        when(localStorageService.saveUserProfilePhoto(mockImage)).thenReturn("profile.jpg");

        // Act
        CustomResponse response = authService.registerUser(registrationRequest, mockImage);

        // Assert
        assertEquals("User registered successfully", response.getMessage());
        verify(userRepository).save(argThat(user -> "profile.jpg".equals(user.getImageUrl())));
    }

    @Test
    void registerUser_usernameOrEmailExists_throwsException() {
        // Arrange
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(EntityRegistrationException.class, () -> authService.registerUser(registrationRequest, null));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_saveFails_throwsException() {
        // Arrange
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(EntityRegistrationException.class, () -> authService.registerUser(registrationRequest, null));
    }

    @Test
    void authenticateUser_success() {
        // Arrange
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken(any(), any())).thenReturn("jwt-token");

        // Act
        LoginResponse response = authService.authenticateUser(loginRequest);

        // Assert
        assertEquals("jwt-token", response.getJwt());
        verify(jwtService).generateToken(any(), eq(user));
    }

    @Test
    void authenticateUser_userNotFound_throwsException() {
        // Arrange
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> authService.authenticateUser(loginRequest));
    }

    @Test
    void authenticateUser_userDisabled_throwsException() {
        // Arrange
        user.setActive(false);
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(UserDisabledException.class, () -> authService.authenticateUser(loginRequest));
    }

    @Test
    void authenticateUser_invalidPassword_throwsException() {
        // Arrange
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(EntityRegistrationException.class, () -> authService.authenticateUser(loginRequest));
    }
}
