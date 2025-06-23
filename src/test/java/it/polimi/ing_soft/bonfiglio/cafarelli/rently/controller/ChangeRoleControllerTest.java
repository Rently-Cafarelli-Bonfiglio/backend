// Test class for ChangeRoleController
package it.polimi.ing_soft.bonfiglio.cafarelli.rently.controller;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.ChangeRoleRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.ChangeRoleResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.ChangeRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChangeRoleControllerTest {

    @Mock
    private ChangeRoleService changeRoleService;

    @InjectMocks
    private ChangeRoleController changeRoleController;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
    }

    @Test
    void requestChangeRole_ShouldReturnOkResponse() {
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setMotivation("I want to become a host");
        CustomResponse mockResponse = new CustomResponse("Request submitted");
        when(changeRoleService.requestChangeRole(request.getMotivation())).thenReturn(mockResponse);

        ResponseEntity<CustomResponse> response = changeRoleController.requestChangeRole(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Request submitted", response.getBody().getMessage());
    }

    @Test
    void findAllChangeRoleRequests_ShouldReturnList() {
        // Crea le istanze di ChangeRoleResponse con i 4 parametri richiesti
        List<ChangeRoleResponse> mockList = List.of(
                new ChangeRoleResponse(1L, "user1", "HOST", "Voglio diventare host"),
                new ChangeRoleResponse(2L, "user2", "HOST", "Anch'io voglio diventare host")
        );

        when(changeRoleService.findAll()).thenReturn(mockList);

        ResponseEntity<List<ChangeRoleResponse>> response = changeRoleController.findAllChangeRoleRequests();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void acceptChangeRole_ShouldReturnOkResponse() {
        Long requestId = 1L;
        CustomResponse mockResponse = new CustomResponse("Request accepted");
        when(changeRoleService.acceptChangeRole(requestId)).thenReturn(mockResponse);

        ResponseEntity<CustomResponse> response = changeRoleController.acceptChangeRole(requestId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Request accepted", response.getBody().getMessage());
    }

    @Test
    void rejectChangeRole_ShouldReturnOkResponse() {
        Long requestId = 2L;
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setMotivation("Not enough activity");
        CustomResponse mockResponse = new CustomResponse("Request rejected");
        when(changeRoleService.rejectChangeRole(requestId, request.getMotivation())).thenReturn(mockResponse);

        ResponseEntity<CustomResponse> response = changeRoleController.rejectChangeRole(requestId, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Request rejected", response.getBody().getMessage());
    }
}
