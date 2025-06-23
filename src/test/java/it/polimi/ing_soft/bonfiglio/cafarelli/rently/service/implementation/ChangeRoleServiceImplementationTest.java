package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.ChangeRoleResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityModifyException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityNotFoundException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChangeRole;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChangeRoleStatus;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Role;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.observer.EventManager;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.ChangeRoleRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChangeRoleServiceImplementationTest {

    @Mock private UserRepository userRepository;
    @Mock private ChangeRoleRepository changeRoleRepository;
    @Mock private EventManager eventManager;

    @InjectMocks private ChangeRoleServiceImplementation changeRoleService;

    private User clientUser;
    private User adminUser;
    private ChangeRole pendingChangeRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup client user
        clientUser = new User();
        clientUser.setId(1L);
        clientUser.setUsername("client");
        clientUser.setRole(Role.CLIENT);
        
        // Setup admin user
        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("admin");
        adminUser.setRole(Role.ADMIN);
        
        // Setup pending change role request
        pendingChangeRole = new ChangeRole(clientUser, "I want to be a host");
        pendingChangeRole.setId(1L);
        pendingChangeRole.setStatus(ChangeRoleStatus.PENDING);
        
        // Set client user as the authenticated user by default
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(clientUser, null, "ROLE_USER")
        );
    }

    @Test
    void requestChangeRole_success() {
        // Arrange
        when(userRepository.findByUsername("client")).thenReturn(Optional.of(clientUser));
        when(changeRoleRepository.findByUserAndStatus(clientUser, ChangeRoleStatus.PENDING)).thenReturn(Optional.empty());
        
        // Act
        CustomResponse response = changeRoleService.requestChangeRole("I want to be a host");
        
        // Assert
        assertEquals("Role change request submitted successfully.", response.getMessage());
        verify(changeRoleRepository).save(any(ChangeRole.class));
    }
    
    @Test
    void requestChangeRole_alreadyPendingRequest_throwsException() {
        // Arrange
        when(userRepository.findByUsername("client")).thenReturn(Optional.of(clientUser));
        when(changeRoleRepository.findByUserAndStatus(clientUser, ChangeRoleStatus.PENDING)).thenReturn(Optional.of(pendingChangeRole));
        
        // Act & Assert
        assertThrows(EntityModifyException.class, () -> changeRoleService.requestChangeRole("Another request"));
        verify(changeRoleRepository, never()).save(any(ChangeRole.class));
    }
    
    @Test
    void requestChangeRole_userAlreadyHost_returnsErrorMessage() {
        // Arrange
        clientUser.setRole(Role.HOST);
        when(userRepository.findByUsername("client")).thenReturn(Optional.of(clientUser));
        
        // Act
        CustomResponse response = changeRoleService.requestChangeRole("I want to be a host");
        
        // Assert
        assertEquals("You can only request a role change from CLIENT to HOST.", response.getMessage());
        verify(changeRoleRepository, never()).save(any(ChangeRole.class));
    }
    
    @Test
    void acceptChangeRole_success() {
        // Arrange
        // Set admin as authenticated user
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(adminUser, null, "ROLE_ADMIN")
        );
        
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(changeRoleRepository.findById(1L)).thenReturn(Optional.of(pendingChangeRole));
        
        // Act
        CustomResponse response = changeRoleService.acceptChangeRole(1L);
        
        // Assert
        assertEquals("Role change request accepted successfully. User client is now a HOST.", response.getMessage());
        assertEquals(ChangeRoleStatus.ACCEPTED, pendingChangeRole.getStatus());
        assertEquals(adminUser, pendingChangeRole.getFullfilledBy());
        assertNotNull(pendingChangeRole.getFullfilledAt());
        verify(changeRoleRepository).save(pendingChangeRole);
        verify(userRepository).save(clientUser);
        verify(eventManager).notify("CHANGEROLE_ACCEPTED", pendingChangeRole);
        assertEquals(Role.HOST, clientUser.getRole());
    }
    
    @Test
    void acceptChangeRole_notAdmin_returnsErrorMessage() {
        // Arrange
        when(userRepository.findByUsername("client")).thenReturn(Optional.of(clientUser));
        
        // Act
        CustomResponse response = changeRoleService.acceptChangeRole(1L);
        
        // Assert
        assertEquals("Only ADMIN users can accept role change requests.", response.getMessage());
        verify(changeRoleRepository, never()).save(any(ChangeRole.class));
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void acceptChangeRole_requestNotFound_throwsException() {
        // Arrange
        // Set admin as authenticated user
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(adminUser, null, "ROLE_ADMIN")
        );
        
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(changeRoleRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> changeRoleService.acceptChangeRole(1L));
    }
    
    @Test
    void acceptChangeRole_requestAlreadyProcessed_returnsErrorMessage() {
        // Arrange
        // Set admin as authenticated user
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(adminUser, null, "ROLE_ADMIN")
        );
        
        pendingChangeRole.setStatus(ChangeRoleStatus.ACCEPTED);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(changeRoleRepository.findById(1L)).thenReturn(Optional.of(pendingChangeRole));
        
        // Act
        CustomResponse response = changeRoleService.acceptChangeRole(1L);
        
        // Assert
        assertEquals("This request has already been accepted.", response.getMessage());
        verify(changeRoleRepository, never()).save(any(ChangeRole.class));
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void rejectChangeRole_success() {
        // Arrange
        // Set admin as authenticated user
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(adminUser, null, "ROLE_ADMIN")
        );
        
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(changeRoleRepository.findById(1L)).thenReturn(Optional.of(pendingChangeRole));
        
        // Act
        CustomResponse response = changeRoleService.rejectChangeRole(1L, "Not enough experience");
        
        // Assert
        assertEquals("Role change request rejected successfully.", response.getMessage());
        assertEquals(ChangeRoleStatus.REJECTED, pendingChangeRole.getStatus());
        assertEquals(adminUser, pendingChangeRole.getFullfilledBy());
        assertNotNull(pendingChangeRole.getFullfilledAt());
        assertEquals("Not enough experience", pendingChangeRole.getMotivation());
        verify(changeRoleRepository).save(pendingChangeRole);
        verify(eventManager).notify("CHANGEROLE_REJECTED", pendingChangeRole);
    }
    
    @Test
    void rejectChangeRole_notAdmin_returnsErrorMessage() {
        // Arrange
        when(userRepository.findByUsername("client")).thenReturn(Optional.of(clientUser));
        
        // Act
        CustomResponse response = changeRoleService.rejectChangeRole(1L, "Not enough experience");
        
        // Assert
        assertEquals("Only ADMIN users can reject role change requests.", response.getMessage());
        verify(changeRoleRepository, never()).save(any(ChangeRole.class));
    }
    
    @Test
    void rejectChangeRole_requestNotFound_throwsException() {
        // Arrange
        // Set admin as authenticated user
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(adminUser, null, "ROLE_ADMIN")
        );
        
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(changeRoleRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> changeRoleService.rejectChangeRole(1L, "Not enough experience"));
    }
    
    @Test
    void rejectChangeRole_requestAlreadyProcessed_returnsErrorMessage() {
        // Arrange
        // Set admin as authenticated user
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(adminUser, null, "ROLE_ADMIN")
        );
        
        pendingChangeRole.setStatus(ChangeRoleStatus.REJECTED);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(changeRoleRepository.findById(1L)).thenReturn(Optional.of(pendingChangeRole));
        
        // Act
        CustomResponse response = changeRoleService.rejectChangeRole(1L, "Not enough experience");
        
        // Assert
        assertEquals("This request has already been rejected.", response.getMessage());
        verify(changeRoleRepository, never()).save(any(ChangeRole.class));
    }
    
    @Test
    void findAll_returnsAllRequests() {
        // Arrange
        ChangeRole acceptedChangeRole = new ChangeRole(clientUser, "I want to be a host");
        acceptedChangeRole.setId(2L);
        acceptedChangeRole.setStatus(ChangeRoleStatus.ACCEPTED);
        
        List<ChangeRole> changeRoles = Arrays.asList(pendingChangeRole, acceptedChangeRole);
        when(changeRoleRepository.findAll()).thenReturn(changeRoles);
        
        // Act
        List<ChangeRoleResponse> responses = changeRoleService.findAll();
        
        // Assert
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals("client", responses.get(0).getUsername());
        assertEquals("I want to be a host", responses.get(0).getMotivation());
        assertEquals("PENDING", responses.get(0).getStatus());
        
        assertEquals(2L, responses.get(1).getId());
        assertEquals("client", responses.get(1).getUsername());
        assertEquals("I want to be a host", responses.get(1).getMotivation());
        assertEquals("ACCEPTED", responses.get(1).getStatus());
    }
}