package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.UserModifyRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.UserPasswordChangeRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.*;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.PropertyRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplementationTest {

    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserRepository userRepository;
    @Mock private PropertyRepository propertyRepository;
    @Mock private LocalStorageService localStorageService;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks private UserServiceImplementation userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurazione utente di test
        user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setEmail("user@example.com");
        user.setPassword("encodedPassword");
        user.setActive(true);
        user.setBalance(BigDecimal.valueOf(100));
        user.setFavorite(new ArrayList<>());

        // Configurazione SecurityContext
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    void modify_success() {
        UserModifyRequest request = new UserModifyRequest("newUser", "new@example.com", "plainPassword");

        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());

        CustomResponse response = userService.modify(request);
        assertEquals("User modified successfully", response.getMessage());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void modify_userInactive_throwsEntityNotFoundException() {
        user.setActive(false);
        UserModifyRequest request = new UserModifyRequest("newUser", "new@example.com", "plainPassword");

        assertThrows(EntityNotFoundException.class, () -> userService.modify(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void modify_wrongPassword_throwsException() {
        UserModifyRequest request = new UserModifyRequest("newUser", "new@example.com", "wrong");
        when(passwordEncoder.matches("wrong", "encodedPassword")).thenReturn(false);

        assertThrows(DataValidationException.class, () -> userService.modify(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void modify_usernameAlreadyUsed_throwsException() {
        UserModifyRequest request = new UserModifyRequest("existingUser", "new@example.com", "plainPassword");
        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);
        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new User()));

        assertThrows(EntityModifyException.class, () -> userService.modify(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void modify_emailAlreadyUsed_throwsException() {
        UserModifyRequest request = new UserModifyRequest("newUser", "taken@example.com", "plainPassword");
        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(EntityModifyException.class, () -> userService.modify(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void modify_repositoryException_returnsErrorResponse() {
        UserModifyRequest request = new UserModifyRequest("newUser", "new@example.com", "plainPassword");
        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("DB error"));

        CustomResponse response = userService.modify(request);
        assertEquals("Error while modifying user", response.getMessage());
    }

    @Test
    void modifyWithImage_success() {
        UserModifyRequest request = new UserModifyRequest("newUser", "new@example.com", "plainPassword");
        MultipartFile mockImage = mock(MultipartFile.class);

        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(mockImage.isEmpty()).thenReturn(false);
        when(localStorageService.updateUserProfilePhoto(mockImage, null)).thenReturn("image.png");

        CustomResponse response = userService.modifyWithImage(request, mockImage);
        assertEquals("User modified successfully with image", response.getMessage());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void modifyWithImage_userInactive_throwsEntityNotFoundException() {
        user.setActive(false);
        UserModifyRequest request = new UserModifyRequest("newUser", "new@example.com", "plainPassword");
        MultipartFile mockImage = mock(MultipartFile.class);

        assertThrows(EntityNotFoundException.class, () -> userService.modifyWithImage(request, mockImage));
        verify(userRepository, never()).save(any());
    }

    @Test
    void modifyWithImage_emptyImage_skipsUpload() {
        UserModifyRequest request = new UserModifyRequest("newUser", "new@example.com", "plainPassword");
        MultipartFile image = mock(MultipartFile.class);

        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(image.isEmpty()).thenReturn(true);

        CustomResponse response = userService.modifyWithImage(request, image);
        assertEquals("User modified successfully with image", response.getMessage());
        verify(localStorageService, never()).updateUserProfilePhoto(any(), any());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void modifyWithImage_repositoryException_returnsErrorResponse() {
        UserModifyRequest request = new UserModifyRequest("newUser", "new@example.com", "plainPassword");
        MultipartFile mockImage = mock(MultipartFile.class);

        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(mockImage.isEmpty()).thenReturn(false);
        when(localStorageService.updateUserProfilePhoto(mockImage, null)).thenReturn("image.png");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("DB error"));

        CustomResponse response = userService.modifyWithImage(request, mockImage);
        assertEquals("Error while modifying user with image", response.getMessage());
    }

    @Test
    void changePassword_success() {
        UserPasswordChangeRequest request = new UserPasswordChangeRequest("old", "new", "new");

        when(passwordEncoder.matches("old", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("newEncoded");

        CustomResponse response = userService.changePassword(request);
        assertEquals("Password changed successfully", response.getMessage());
        verify(userRepository).save(user);
        assertEquals("newEncoded", user.getPassword());
    }

    @Test
    void changePassword_userInactive_throwsEntityNotFoundException() {
        user.setActive(false);
        UserPasswordChangeRequest request = new UserPasswordChangeRequest("old", "new", "new");

        assertThrows(EntityNotFoundException.class, () -> userService.changePassword(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_wrongOldPassword_returnsErrorMessage() {
        UserPasswordChangeRequest request = new UserPasswordChangeRequest("wrongOld", "new", "new");
        when(passwordEncoder.matches("wrongOld", "encodedPassword")).thenReturn(false);

        CustomResponse response = userService.changePassword(request);
        assertEquals("The old password is not correct", response.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_passwordsMismatch_returnsErrorMessage() {
        UserPasswordChangeRequest request = new UserPasswordChangeRequest("old", "new", "different");
        when(passwordEncoder.matches("old", "encodedPassword")).thenReturn(true);

        CustomResponse response = userService.changePassword(request);
        assertEquals("The passwords do not match", response.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_repositoryException_throwsEntityModifyException() {
        UserPasswordChangeRequest request = new UserPasswordChangeRequest("old", "new", "new");
        when(passwordEncoder.matches("old", "encodedPassword")).thenReturn(true);
        when(userRepository.save(user)).thenThrow(new RuntimeException("DB error"));

        assertThrows(EntityModifyException.class, () -> userService.changePassword(request));
    }

    @Test
    void disable_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        CustomResponse response = userService.disable(1L);
        assertEquals("User disabled successfully", response.getMessage());
        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    void disable_userNotFound_throwsEntityNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.disable(1L));
        verify(userRepository, never()).save(any());
    }

    @Test
    void disable_alreadyDisabled_throwsUserDisabledException() {
        user.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(UserDisabledException.class, () -> userService.disable(1L));
        verify(userRepository, never()).save(any());
    }

    @Test
    void disable_repositoryException_throwsEntityNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenThrow(new RuntimeException("DB error"));

        assertThrows(EntityNotFoundException.class, () -> userService.disable(1L));
    }

    @Test
    void enable_success() {
        user.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        CustomResponse response = userService.enable(1L);
        assertEquals("User enabled successfully", response.getMessage());
        assertTrue(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    void enable_userNotFound_throwsEntityNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.enable(1L));
        verify(userRepository, never()).save(any());
    }

    @Test
    void enable_alreadyEnabled_throwsUserEnabledException() {
        user.setActive(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(UserEnabledException.class, () -> userService.enable(1L));
        verify(userRepository, never()).save(any());
    }

    @Test
    void enable_repositoryException_throwsEntityNotFoundException() {
        user.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenThrow(new RuntimeException("DB error"));

        assertThrows(EntityNotFoundException.class, () -> userService.enable(1L));
    }

    @Test
    void findAll_returnsAllUsers() {
        List<User> users = List.of(user, new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAll();

        assertEquals(users, result);
        verify(userRepository).findAll();
    }

    @Test
    void findByUsername_success() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("user");

        assertEquals(user, result);
        verify(userRepository).findByUsername("user");
    }

    @Test
    void findByUsername_userNotFound_throwsEntityNotFoundException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findByUsername("nonexistent"));
    }

    @Test
    void findById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertEquals(user, result);
        verify(userRepository).findById(1L);
    }

    @Test
    void findById_userNotFound_throwsEntityNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findById(999L));
    }

    @Test
    void getCurrentUser_returnsAuthenticatedUser() {
        User current = userService.getCurrentUser();
        assertEquals(user, current);
    }

    @Test
    void addFavoriteProperty_success() {
        Property property = new Property();
        property.setId(10L);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.addFavoriteProperty("user", 10L);
        assertTrue(result.getFavorite().contains(property));
        verify(userRepository).save(user);
    }

    @Test
    void addFavoriteProperty_userNotFound_throwsEntityNotFoundException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.addFavoriteProperty("nonexistent", 10L));
        verify(propertyRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void addFavoriteProperty_propertyNotFound_throwsEntityNotFoundException() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(propertyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.addFavoriteProperty("user", 999L));
        verify(userRepository, never()).save(any());
    }

    @Test
    void addFavoriteProperty_propertyAlreadyInFavorites_doesNotAddDuplicate() {
        Property property = new Property();
        property.setId(10L);
        user.getFavorite().add(property);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.addFavoriteProperty("user", 10L);
        assertEquals(1, result.getFavorite().size());
        verify(userRepository).save(user);
    }

    @Test
    void removeFavoriteProperty_success() {
        Property property = new Property();
        property.setId(10L);
        user.getFavorite().add(property);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.removeFavoriteProperty("user", 10L);
        assertFalse(result.getFavorite().contains(property));
        verify(userRepository).save(user);
    }

    @Test
    void removeFavoriteProperty_userNotFound_throwsEntityNotFoundException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.removeFavoriteProperty("nonexistent", 10L));
        verify(propertyRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void removeFavoriteProperty_propertyNotFound_throwsEntityNotFoundException() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(propertyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.removeFavoriteProperty("user", 999L));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getAllFavoriteProperties_success() {
        Property property = new Property();
        property.setId(10L);
        user.getFavorite().add(property);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        List<Property> result = userService.getAllFavoriteProperties("user");

        assertEquals(1, result.size());
        assertEquals(property, result.get(0));
    }

    @Test
    void getAllFavoriteProperties_userNotFound_throwsEntityNotFoundException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getAllFavoriteProperties("nonexistent"));
    }

    @Test
    void rechargeBalance_success() {
        BigDecimal initialBalance = user.getBalance();
        BigDecimal amount = BigDecimal.valueOf(50);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.rechargeBalance("user", amount);

        assertEquals(initialBalance.add(amount), result.getBalance());
        verify(userRepository).save(user);
    }

    @Test
    void rechargeBalance_userNotFound_throwsEntityNotFoundException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.rechargeBalance("nonexistent", BigDecimal.TEN));
        verify(userRepository, never()).save(any());
    }

    @Test
    void deductBalance_sufficientFunds_returnsTrue() {
        BigDecimal amount = BigDecimal.valueOf(50);
        BigDecimal initialBalance = user.getBalance();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        boolean result = userService.deductBalance("user", amount);

        assertTrue(result);
        assertEquals(initialBalance.subtract(amount), user.getBalance());
        verify(userRepository).save(user);
    }

    @Test
    void deductBalance_insufficientFunds_returnsFalse() {
        BigDecimal amount = BigDecimal.valueOf(200);
        BigDecimal initialBalance = user.getBalance();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        boolean result = userService.deductBalance("user", amount);

        assertFalse(result);
        assertEquals(initialBalance, user.getBalance());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deductBalance_userNotFound_throwsEntityNotFoundException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.deductBalance("nonexistent", BigDecimal.TEN));
        verify(userRepository, never()).save(any());
    }
}