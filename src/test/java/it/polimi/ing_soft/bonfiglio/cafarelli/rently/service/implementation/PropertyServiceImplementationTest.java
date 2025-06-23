package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.AvailablePropertiesRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.PropertyCreateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.PropertyUpdateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.DataValidationException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityNotFoundException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityRegistrationException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.PropertyAlreadyExistsException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Role;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.BookingRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.PropertyRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PropertyServiceImplementationTest {

    @Mock private PropertyRepository propertyRepository;
    @Mock private UserRepository userRepository;
    @Mock private LocalStorageService localStorageService;
    @Mock private BookingRepository bookingRepository;
    @Mock private MultipartFile mockImage;

    @InjectMocks private PropertyServiceImplementation propertyService;

    private User host;
    private User admin;
    private Property property;
    private PropertyCreateRequest createRequest;
    private PropertyUpdateRequest updateRequest;
    private List<MultipartFile> images;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup host user
        host = new User();
        host.setId(1L);
        host.setUsername("host");
        host.setRole(Role.HOST);

        // Setup admin user
        admin = new User();
        admin.setId(2L);
        admin.setUsername("admin");
        admin.setRole(Role.ADMIN);

        // Setup property
        property = new Property();
        property.setId(1L);
        property.setTitle("Test Property");
        property.setDescription("A test property");
        property.setAddress("123 Test St");
        property.setCity("Test City");
        property.setState("Test State");
        property.setCountry("Test Country");
        property.setPricePerNight(new BigDecimal("100"));
        property.setMaxGuests(4);
        property.setBedrooms(2);
        property.setBathrooms(1);
        property.setAvailable(true);
        property.setHost(host);
        property.setPropertyImages(new ArrayList<>());

        // Setup create request
        createRequest = new PropertyCreateRequest();
        createRequest.setTitle("New Property");
        createRequest.setDescription("A new property");
        createRequest.setAddress("456 New St");
        createRequest.setCity("New City");
        createRequest.setState("New State");
        createRequest.setCountry("New Country");
        createRequest.setPricePerNight(new BigDecimal("150"));
        createRequest.setMaxGuests(6);
        createRequest.setBedrooms(3);
        createRequest.setBathrooms(2);

        // Setup update request
        updateRequest = new PropertyUpdateRequest();
        updateRequest.setTitle("Test Property");
        updateRequest.setDescription("Updated description");
        updateRequest.setPricePerNight(new BigDecimal("120"));
        updateRequest.setMaxGuests(5);
        updateRequest.setBedrooms(2);
        updateRequest.setBathrooms(2);

        // Setup images
        images = new ArrayList<>();
        images.add(mockImage);

        // Set host as the authenticated user by default
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(host, null)
        );
    }

    @Test
    void createProperty_success() {
        // Arrange
        when(userRepository.findByUsername("host")).thenReturn(Optional.of(host));
        when(propertyRepository.findByTitleAndHostId("New Property", 1L)).thenReturn(Optional.empty());
        when(localStorageService.savePropertyImages(images, 1L)).thenReturn(Arrays.asList("image1.jpg"));

        // Act
        CustomResponse response = propertyService.createProperty(createRequest, images);

        // Assert
        assertEquals("Property created successfully", response.getMessage());
        verify(propertyRepository).save(any(Property.class));
        verify(localStorageService).savePropertyImages(images, 1L);
    }

    @Test
    void createProperty_propertyAlreadyExists_throwsException() {
        // Arrange
        when(userRepository.findByUsername("host")).thenReturn(Optional.of(host));
        when(propertyRepository.findByTitleAndHostId("New Property", 1L)).thenReturn(Optional.of(property));

        // Act & Assert
        assertThrows(PropertyAlreadyExistsException.class, () -> 
            propertyService.createProperty(createRequest, images)
        );
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    void createProperty_saveFails_throwsException() {
        // Arrange
        when(userRepository.findByUsername("host")).thenReturn(Optional.of(host));
        when(propertyRepository.findByTitleAndHostId("New Property", 1L)).thenReturn(Optional.empty());
        when(localStorageService.savePropertyImages(images, 1L)).thenReturn(Arrays.asList("image1.jpg"));
        when(propertyRepository.save(any(Property.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(EntityRegistrationException.class, () -> 
            propertyService.createProperty(createRequest, images)
        );
    }

    @Test
    void updateProperty_success() {
        // Arrange
        when(propertyRepository.findByTitle("Test Property")).thenReturn(Optional.of(property));

        // Act
        CustomResponse response = propertyService.updateProperty(updateRequest);

        // Assert
        assertEquals("Property modified successfully", response.getMessage());
        assertEquals("Updated description", property.getDescription());
        assertEquals(new BigDecimal("120"), property.getPricePerNight());
        assertEquals(5, property.getMaxGuests());
        assertEquals(2, property.getBathrooms());
        verify(propertyRepository).save(property);
    }

    @Test
    void updateProperty_propertyNotFound_throwsException() {
        // Arrange
        when(propertyRepository.findByTitle("Test Property")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            propertyService.updateProperty(updateRequest)
        );
    }

    @Test
    void updateProperty_notAuthorized_returnsErrorMessage() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(3L);
        otherUser.setUsername("other");

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(otherUser, null)
        );

        when(propertyRepository.findByTitle("Test Property")).thenReturn(Optional.of(property));

        // Act
        CustomResponse response = propertyService.updateProperty(updateRequest);

        // Assert
        assertEquals("You are not authorized to update this property", response.getMessage());
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    void updatePropertyWithImages_success() {
        // Arrange
        when(propertyRepository.findByTitle("Test Property")).thenReturn(Optional.of(property));
        when(localStorageService.savePropertyImages(images, 1L)).thenReturn(Arrays.asList("newimage1.jpg"));

        // Act
        CustomResponse response = propertyService.updateProperty(updateRequest, images);

        // Assert
        assertEquals("Property modified successfully", response.getMessage());
        assertEquals("Updated description", property.getDescription());
        assertEquals(new BigDecimal("120"), property.getPricePerNight());
        assertEquals(5, property.getMaxGuests());
        assertEquals(2, property.getBathrooms());
        assertEquals(Arrays.asList("newimage1.jpg"), property.getPropertyImages());
        verify(propertyRepository).save(property);
        verify(localStorageService).savePropertyImages(images, 1L);
    }

    @Test
    void deleteProperty_success() {
        // Arrange
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(bookingRepository.existsByPropertyId(1L)).thenReturn(false);

        // Act
        CustomResponse response = propertyService.deleteProperty(1L);

        // Assert
        assertEquals("Property deleted successfully", response.getMessage());
        verify(propertyRepository).delete(property);
    }

    @Test
    void deleteProperty_propertyNotFound_throwsException() {
        // Arrange
        when(propertyRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            propertyService.deleteProperty(999L)
        );
    }

    @Test
    void deleteProperty_notAuthorized_returnsErrorMessage() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(3L);
        otherUser.setUsername("other");
        otherUser.setRole(Role.CLIENT);

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(otherUser, null)
        );

        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        // Act
        CustomResponse response = propertyService.deleteProperty(1L);

        // Assert
        assertEquals("You are not authorized to delete this property", response.getMessage());
        verify(propertyRepository, never()).delete(any(Property.class));
    }

    @Test
    void deleteProperty_hasBookings_returnsErrorMessage() {
        // Arrange
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(bookingRepository.existsByPropertyId(1L)).thenReturn(true);

        // Act
        CustomResponse response = propertyService.deleteProperty(1L);

        // Assert
        assertEquals("Cannot delete property with existing bookings", response.getMessage());
        verify(propertyRepository, never()).delete(any(Property.class));
    }

    @Test
    void findById_success() {
        // Arrange
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        // Act
        Property result = propertyService.findById(1L);

        // Assert
        assertEquals(property, result);
    }

    @Test
    void findById_propertyNotFound_throwsException() {
        // Arrange
        when(propertyRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            propertyService.findById(999L)
        );
    }

    @Test
    void findAll_returnsAllProperties() {
        // Arrange
        List<Property> properties = Arrays.asList(property);
        when(propertyRepository.findAll()).thenReturn(properties);

        // Act
        List<Property> result = propertyService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(property, result.get(0));
    }

    @Test
    void findByHostId_returnsHostProperties() {
        // Arrange
        List<Property> properties = Arrays.asList(property);
        when(propertyRepository.findByHostId(1L)).thenReturn(properties);

        // Act
        List<Property> result = propertyService.findByHostId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(property, result.get(0));
    }

    @Test
    void findByCity_returnsCityProperties() {
        // Arrange
        List<Property> properties = Arrays.asList(property);
        when(propertyRepository.findByCity("Test City")).thenReturn(properties);

        // Act
        List<Property> result = propertyService.findByCity("Test City");

        // Assert
        assertEquals(1, result.size());
        assertEquals(property, result.get(0));
    }

    @Test
    void findAvailable_returnsAvailableProperties() {
        // Arrange
        List<Property> properties = Arrays.asList(property);
        when(propertyRepository.findByIsAvailableIsTrue()).thenReturn(properties);

        // Act
        List<Property> result = propertyService.findAvailable();

        // Assert
        assertEquals(1, result.size());
        assertEquals(property, result.get(0));
    }

    @Test
    void toggleActiveStatus_success() {
        // Arrange
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(propertyRepository.save(property)).thenReturn(property);

        // Act
        CustomResponse response = propertyService.toggleActiveStatus(1L);

        // Assert
        assertEquals("Property deactivated successfully", response.getMessage());
        assertFalse(property.isAvailable());
        verify(propertyRepository).save(property);
    }

    @Test
    void toggleActiveStatus_propertyNotFound_throwsException() {
        // Arrange
        when(propertyRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            propertyService.toggleActiveStatus(999L)
        );
    }

    @Test
    void toggleActiveStatus_notAuthorized_returnsErrorMessage() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(3L);
        otherUser.setUsername("other");

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(otherUser, null)
        );

        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        // Act
        CustomResponse response = propertyService.toggleActiveStatus(1L);

        // Assert
        assertEquals("You are not authorized to modify this property", response.getMessage());
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    void findAvailableProperties_success() {
        // Arrange
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = LocalDate.now().plusDays(5);
        AvailablePropertiesRequest request = new AvailablePropertiesRequest(
            checkInDate,
            checkOutDate,
            "Test City",
            2,
            1
        );

        List<Property> properties = Arrays.asList(property);
        when(propertyRepository.findAvailableProperties(
                "Test City", 
                request.getCheckInDate(), 
                request.getCheckOutDate(), 
                3)).thenReturn(properties);

        // Act
        List<Property> result = propertyService.findAvailableProperties(request);

        // Assert
        assertEquals(1, result.size());
        assertEquals(property, result.get(0));
    }

    @Test
    void findAvailableProperties_invalidDates_throwsException() {
        // Arrange
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = LocalDate.now().minusDays(1); // Invalid: checkout before checkin
        AvailablePropertiesRequest request = new AvailablePropertiesRequest(
            checkInDate,
            checkOutDate,
            "Test City",
            2,
            1
        );

        // Act & Assert
        assertThrows(DataValidationException.class, () -> 
            propertyService.findAvailableProperties(request)
        );
    }
}
