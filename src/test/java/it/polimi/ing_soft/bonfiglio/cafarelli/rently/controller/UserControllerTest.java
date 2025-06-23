package it.polimi.ing_soft.bonfiglio.cafarelli.rently.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.UserModifyRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.UserPasswordChangeRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Validator validator;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void modify_WithoutImage_ShouldReturnSuccessResponse() throws IOException {
        // Preparazione
        UserModifyRequest request = new UserModifyRequest();
        request.setUsername("testuser");
        request.setEmail("testUser@mail.com");

        String userModifyRequestJson = new ObjectMapper().writeValueAsString(request);

        CustomResponse mockResponse = new CustomResponse("Profile updated successfully");

        when(validator.validate(any(UserModifyRequest.class))).thenReturn(Set.of());
        when(userService.modify(any(UserModifyRequest.class))).thenReturn(mockResponse);

        // Esecuzione
        ResponseEntity<CustomResponse> response = userController.modify(userModifyRequestJson, null);

        // Verifica
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Profile updated successfully", response.getBody().getMessage());
        verify(userService).modify(any(UserModifyRequest.class));
        verify(userService, never()).modifyWithImage(any(), any());
    }

    @Test
    void modify_WithImage_ShouldReturnSuccessResponse() throws IOException {
        // Preparazione
        UserModifyRequest request = new UserModifyRequest();
        request.setUsername("testuser");
        request.setEmail("testUser@mail.com");

        String userModifyRequestJson = new ObjectMapper().writeValueAsString(request);

        MultipartFile mockImage = mock(MultipartFile.class);
        when(mockImage.isEmpty()).thenReturn(false);

        CustomResponse mockResponse = new CustomResponse("Profile with image updated successfully");

        when(validator.validate(any(UserModifyRequest.class))).thenReturn(Set.of());
        when(userService.modifyWithImage(any(UserModifyRequest.class), eq(mockImage))).thenReturn(mockResponse);

        // Esecuzione
        ResponseEntity<CustomResponse> response = userController.modify(userModifyRequestJson, mockImage);

        // Verifica
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Profile with image updated successfully", response.getBody().getMessage());
        verify(userService).modifyWithImage(any(UserModifyRequest.class), eq(mockImage));
        verify(userService, never()).modify(any());
    }

    @Test
    void modify_WithInvalidRequest_ShouldThrowConstraintViolationException() throws IOException {
        // Preparazione
        UserModifyRequest request = new UserModifyRequest();
        String userModifyRequestJson = new ObjectMapper().writeValueAsString(request);

        Set<ConstraintViolation<UserModifyRequest>> violations = Set.of(mock(ConstraintViolation.class));
        when(validator.validate(any(UserModifyRequest.class))).thenReturn(violations);

        // Esecuzione e verifica
        assertThrows(ConstraintViolationException.class, () ->
                userController.modify(userModifyRequestJson, null));

        verify(userService, never()).modify(any());
        verify(userService, never()).modifyWithImage(any(), any());
    }

    @Test
    void changePassword_ShouldReturnSuccessResponse() {
        UserPasswordChangeRequest request = new UserPasswordChangeRequest("oldPassword", "newPassword", "newPassword");
        CustomResponse mockResponse = new CustomResponse("Password changed");

        when(userService.changePassword(request)).thenReturn(mockResponse);

        ResponseEntity<CustomResponse> response = userController.changePassword(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Password changed", response.getBody().getMessage());
    }

    @Test
    void disableUser_ShouldReturnSuccessResponse() {
        Long userId = 1L;
        CustomResponse mockResponse = new CustomResponse("User disabled");

        when(userService.disable(userId)).thenReturn(mockResponse);

        ResponseEntity<CustomResponse> response = userController.disable(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User disabled", response.getBody().getMessage());
    }

    @Test
    void enableUser_ShouldReturnSuccessResponse() {
        Long userId = 1L;
        CustomResponse mockResponse = new CustomResponse("User enabled");

        when(userService.enable(userId)).thenReturn(mockResponse);

        ResponseEntity<CustomResponse> response = userController.enable(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User enabled", response.getBody().getMessage());
    }

    @Test
    void getCurrentUser_ShouldReturnUser() {
        User user = new User();
        user.setId(1L);

        when(userService.getCurrentUser()).thenReturn(user);

        ResponseEntity<User> response = userController.getCurrentUser();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void getAllUsers_ShouldReturnUserList() {
        List<User> users = List.of(new User(), new User());

        when(userService.findAll()).thenReturn(users);

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getAllFavoriteProperties_ShouldReturnPropertyList() {
        List<Property> properties = List.of(new Property(), new Property());

        when(userService.getAllFavoriteProperties("testuser")).thenReturn(properties);

        ResponseEntity<List<Property>> response = userController.getAllFavoriteProperties("testuser");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void addFavoriteProperty_ShouldReturnUpdatedUser() {
        User user = new User();

        when(userService.addFavoriteProperty("testuser", 1L)).thenReturn(user);

        ResponseEntity<User> response = userController.addFavoriteProperty("testuser", 1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
    }

    @Test
    void removeFavoriteProperty_ShouldReturnUpdatedUser() {
        User user = new User();

        when(userService.removeFavoriteProperty("testuser", 1L)).thenReturn(user);

        ResponseEntity<User> response = userController.removeFavoriteProperty("testuser", 1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
    }

    @Test
    void rechargeBalance_ShouldReturnUpdatedUser() {
        User user = new User();
        BigDecimal amount = new BigDecimal("100.00");

        when(userService.rechargeBalance("testuser", amount)).thenReturn(user);

        ResponseEntity<User> response = userController.rechargeBalance("testuser", amount);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
    }
}