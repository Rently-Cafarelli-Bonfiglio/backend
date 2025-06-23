package it.polimi.ing_soft.bonfiglio.cafarelli.rently.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.AvailablePropertiesRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.PropertyCreateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.PropertyUpdateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.PropertyService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PropertyControllerTest {

    @Mock
    private PropertyService propertyService;

    @Mock
    private Validator validator;

    @Mock
    private Set<ConstraintViolation<Object>> violations;

    @InjectMocks
    private PropertyController propertyController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private PropertyCreateRequest createRequest;
    private PropertyUpdateRequest updateRequest;
    private String createRequestJson;
    private String updateRequestJson;
    private List<MultipartFile> mockImages;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // Configurazione mock comuni
        createRequest = new PropertyCreateRequest();
        createRequestJson = objectMapper.writeValueAsString(createRequest);

        updateRequest = new PropertyUpdateRequest();
        updateRequestJson = objectMapper.writeValueAsString(updateRequest);

        // Creazione mock immagini
        mockImages = List.of(
                new MockMultipartFile("image1", "image1.jpg", "image/jpeg", "test image content".getBytes()),
                new MockMultipartFile("image2", "image2.jpg", "image/jpeg", "test image content".getBytes())
        );

        // Mock del validator
        when(validator.validate(any())).thenReturn(Collections.emptySet());
    }

    @Test
    void createProperty_Success() throws IOException {
        CustomResponse expectedResponse = new CustomResponse("Property created successfully");
        when(propertyService.createProperty(any(PropertyCreateRequest.class), eq(mockImages)))
                .thenReturn(expectedResponse);

        ResponseEntity<CustomResponse> response = propertyController.createProperty(createRequestJson, mockImages);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(propertyService).createProperty(any(PropertyCreateRequest.class), eq(mockImages));
    }

    @Test
    void createProperty_ValidationError(){
        when(validator.validate(any())).thenReturn(Set.of(mock(ConstraintViolation.class)));

        assertThrows(ConstraintViolationException.class,
                () -> propertyController.createProperty(createRequestJson, mockImages));

        verify(propertyService, never()).createProperty(any(), any());
    }

    @Test
    void updateProperty_Success() throws IOException {
        CustomResponse expectedResponse = new CustomResponse("Property updated successfully");
        when(propertyService.updateProperty(any(PropertyUpdateRequest.class), eq(mockImages)))
                .thenReturn(expectedResponse);

        ResponseEntity<CustomResponse> response = propertyController.updateProperty(updateRequestJson, mockImages);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(propertyService).updateProperty(any(PropertyUpdateRequest.class), eq(mockImages));
    }

    @Test
    void updateProperty_NoImages_Success() throws IOException {
        CustomResponse expectedResponse = new CustomResponse("Property updated successfully");
        when(propertyService.updateProperty(any(PropertyUpdateRequest.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<CustomResponse> response = propertyController.updateProperty(updateRequestJson, null);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(propertyService).updateProperty(any(PropertyUpdateRequest.class));
        verify(propertyService, never()).updateProperty(any(PropertyUpdateRequest.class), any());
    }

    @Test
    void updateProperty_EmptyImages_Success() throws IOException {
        CustomResponse expectedResponse = new CustomResponse("Property updated successfully");
        when(propertyService.updateProperty(any(PropertyUpdateRequest.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<CustomResponse> response = propertyController.updateProperty(updateRequestJson, Collections.emptyList());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(propertyService).updateProperty(any(PropertyUpdateRequest.class));
    }

    @Test
    void updateProperty_ValidationError(){
        when(validator.validate(any())).thenReturn(Set.of(mock(ConstraintViolation.class)));

        assertThrows(ConstraintViolationException.class,
                () -> propertyController.updateProperty(updateRequestJson, mockImages));

        verify(propertyService, never()).updateProperty(any());
        verify(propertyService, never()).updateProperty(any(), any());
    }

    @Test
    void deleteProperty_Success() {
        CustomResponse expectedResponse = new CustomResponse("Property deleted successfully");
        when(propertyService.deleteProperty(1L)).thenReturn(expectedResponse);

        ResponseEntity<CustomResponse> response = propertyController.deleteProperty(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(propertyService).deleteProperty(1L);
    }

    @Test
    void getPropertyById_Success() {
        Property property = new Property();
        property.setId(1L);
        when(propertyService.findById(1L)).thenReturn(property);

        ResponseEntity<Property> response = propertyController.getPropertyById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(property, response.getBody());
        verify(propertyService).findById(1L);
    }

    @Test
    void getAllProperties_Success() {
        List<Property> properties = List.of(new Property(), new Property());
        when(propertyService.findAll()).thenReturn(properties);

        ResponseEntity<List<Property>> response = propertyController.getAllProperties();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(properties, response.getBody());
        assertEquals(2, response.getBody().size());
        verify(propertyService).findAll();
    }

    @Test
    void getAllProperties_EmptyList_Success() {
        when(propertyService.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Property>> response = propertyController.getAllProperties();

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
        verify(propertyService).findAll();
    }

    @Test
    void getPropertiesByHostId_Success() {
        List<Property> properties = List.of(new Property(), new Property());
        when(propertyService.findByHostId(1L)).thenReturn(properties);

        ResponseEntity<List<Property>> response = propertyController.getPropertiesByHostId(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(properties, response.getBody());
        assertEquals(2, response.getBody().size());
        verify(propertyService).findByHostId(1L);
    }

    @Test
    void getPropertiesByHostId_NoPropertiesFound_ReturnsEmptyList() {
        when(propertyService.findByHostId(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Property>> response = propertyController.getPropertiesByHostId(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
        verify(propertyService).findByHostId(1L);
    }

    @Test
    void getPropertiesByCity_Success() {
        List<Property> properties = List.of(new Property(), new Property());
        when(propertyService.findByCity("Milano")).thenReturn(properties);

        ResponseEntity<List<Property>> response = propertyController.getPropertiesByCity("Milano");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(properties, response.getBody());
        assertEquals(2, response.getBody().size());
        verify(propertyService).findByCity("Milano");
    }

    @Test
    void getPropertiesByCity_NoCityMatch_ReturnsEmptyList() {
        when(propertyService.findByCity("NonExistentCity")).thenReturn(Collections.emptyList());

        ResponseEntity<List<Property>> response = propertyController.getPropertiesByCity("NonExistentCity");

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
        verify(propertyService).findByCity("NonExistentCity");
    }

    @Test
    void getAvailableProperties_Success() {
        List<Property> properties = List.of(new Property(), new Property());
        when(propertyService.findAvailable()).thenReturn(properties);

        ResponseEntity<List<Property>> response = propertyController.getAvailableProperties();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(properties, response.getBody());
        assertEquals(2, response.getBody().size());
        verify(propertyService).findAvailable();
    }

    @Test
    void getAvailableProperties_NoAvailableProperties_ReturnsEmptyList() {
        when(propertyService.findAvailable()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Property>> response = propertyController.getAvailableProperties();

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
        verify(propertyService).findAvailable();
    }

    @Test
    void toggleActiveStatus_Success() {
        CustomResponse expectedResponse = new CustomResponse("Property status toggled successfully");
        when(propertyService.toggleActiveStatus(1L)).thenReturn(expectedResponse);

        ResponseEntity<CustomResponse> response = propertyController.toggleActiveStatus(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(propertyService).toggleActiveStatus(1L);
    }

    @Test
    void searchAvailableProperties_Success() {
        AvailablePropertiesRequest request = new AvailablePropertiesRequest(
                LocalDate.now(), LocalDate.now().plusDays(5), "Milano", 2, 1);

        List<Property> properties = List.of(new Property(), new Property());
        when(propertyService.findAvailableProperties(request)).thenReturn(properties);

        ResponseEntity<List<Property>> response = propertyController.searchAvailableProperties(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(properties, response.getBody());
        assertEquals(2, response.getBody().size());
        verify(propertyService).findAvailableProperties(request);
    }

    @Test
    void searchAvailableProperties_NoMatchingProperties_ReturnsEmptyList() {
        AvailablePropertiesRequest request = new AvailablePropertiesRequest(
                LocalDate.now(), LocalDate.now().plusDays(5), "Milano", 2, 1);

        when(propertyService.findAvailableProperties(request)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Property>> response = propertyController.searchAvailableProperties(request);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
        verify(propertyService).findAvailableProperties(request);
    }
}