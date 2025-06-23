package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.implementation.PropertyBuilderImplementation;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.AvailablePropertiesRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.PropertyCreateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.PropertyUpdateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.DataValidationException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityNotFoundException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityRegistrationException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.PropertyAlreadyExistsException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.BookingRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.PropertyRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.UserRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.PropertyService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * This class implements the PropertyService interface and provides methods for managing properties.
 * It uses the PropertyRepository and UserRepository to interact with the database.
 */
@Service
@RequiredArgsConstructor
public class PropertyServiceImplementation implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final LocalStorageService localStorageService;
    private final BookingRepository bookingRepository;

    @Override
    public CustomResponse createProperty(@NonNull PropertyCreateRequest request, @NotNull List<MultipartFile> images) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User host = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Property property = new PropertyBuilderImplementation()
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .address(request.getAddress())
                        .city(request.getCity())
                        .state(request.getState())
                        .country(request.getCountry())
                        .pricePerNight(request.getPricePerNight())
                        .maxGuests(request.getMaxGuests())
                        .bedrooms(request.getBedrooms())
                        .bathrooms(request.getBathrooms())
                        .isAvailable(true)
                        .host(host)
                        .build();

        Optional<Property> propertyOpt = propertyRepository.findByTitleAndHostId(request.getTitle(), host.getId());

        if (propertyOpt.isPresent()) {
            throw new PropertyAlreadyExistsException(" Property with title '" + request.getTitle() + "' already exists for this host.");
        }

        List<String> pathToPropertyImages = localStorageService.savePropertyImages(images, host.getId());

        property.setPropertyImages(pathToPropertyImages);

        try {
            propertyRepository.save(property);
        } catch (Exception e) {
            throw new EntityRegistrationException(Property.class);
        }

        return new CustomResponse("Property created successfully");
    }


    @Override
    public CustomResponse updateProperty(@NonNull PropertyUpdateRequest propertyUpdateRequest) {
        Optional<Property> property = propertyRepository.findByTitle(propertyUpdateRequest.getTitle());
        if (property.isEmpty()) {
            throw new EntityNotFoundException(Property.class);
        }

        User currentUser = getCurrentUser();
        Property propertyEntity = property.get();

        if (!propertyEntity.getHost().getId().equals(currentUser.getId())) {
            return new CustomResponse("You are not authorized to update this property");
        }

        Property modifiedProperty = property.get();
        modifiedProperty.setTitle(propertyUpdateRequest.getTitle());
        modifiedProperty.setDescription(propertyUpdateRequest.getDescription());
        modifiedProperty.setPricePerNight(propertyUpdateRequest.getPricePerNight());
        modifiedProperty.setBedrooms(propertyUpdateRequest.getBedrooms());
        modifiedProperty.setBathrooms(propertyUpdateRequest.getBathrooms());
        modifiedProperty.setMaxGuests(propertyUpdateRequest.getMaxGuests());

        try {
            propertyRepository.save(modifiedProperty);
        } catch (Exception e) {
            return new CustomResponse("Error modifying property");
        }

        return new CustomResponse("Property modified successfully");
    }

    @Override
    public CustomResponse updateProperty(@NonNull PropertyUpdateRequest propertyUpdateRequest, List<MultipartFile> images) {
        Optional<Property> property = propertyRepository.findByTitle(propertyUpdateRequest.getTitle());
        if (property.isEmpty()) {
            throw new EntityNotFoundException(Property.class);
        }

        User currentUser = getCurrentUser();
        Property propertyEntity = property.get();

        if (!propertyEntity.getHost().getId().equals(currentUser.getId())) {
            return new CustomResponse("You are not authorized to update this property");
        }

        Property modifiedProperty = property.get();
        modifiedProperty.setTitle(propertyUpdateRequest.getTitle());
        modifiedProperty.setDescription(propertyUpdateRequest.getDescription());
        modifiedProperty.setPricePerNight(propertyUpdateRequest.getPricePerNight());
        modifiedProperty.setBedrooms(propertyUpdateRequest.getBedrooms());
        modifiedProperty.setBathrooms(propertyUpdateRequest.getBathrooms());
        modifiedProperty.setMaxGuests(propertyUpdateRequest.getMaxGuests());

        List<String> pathToPropertyImages = localStorageService.savePropertyImages(images, currentUser.getId());

        property.get().setPropertyImages(pathToPropertyImages);

        try {
            propertyRepository.save(modifiedProperty);
        } catch (Exception e) {
            return new CustomResponse("Error modifying property");
        }

        return new CustomResponse("Property modified successfully");
    }

    @Override
    public CustomResponse deleteProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException(Property.class));

        User currentUser = getCurrentUser();

        if (!property.getHost().getId().equals(currentUser.getId()) && !currentUser.getRole().equals("ADMIN") && !currentUser.getRole().equals("SUPER_ADMIN")) {
            return new CustomResponse("You are not authorized to delete this property");
        }

        // Check if there are any bookings associated with the property
        if (bookingRepository.existsByPropertyId(propertyId)) {
            return new CustomResponse("Cannot delete property with existing bookings");
        }

        try {
            propertyRepository.delete(property);
        } catch (Exception e) {
            return new CustomResponse("Error deleting property");
        }

        return new CustomResponse("Property deleted successfully");
    }
    @Override
    public Property findById(@NonNull Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with ID: " + propertyId));
    }

    @Override
    public List<Property> findAll() {
        return propertyRepository.findAll();
    }

    @Override
    public List<Property> findByHostId(@NonNull Long hostId) {
        return propertyRepository.findByHostId(hostId);
    }

    @Override
    public List<Property> findByCity(String city) {
        return propertyRepository.findByCity(city);
    }

    @Override
    public List<Property> findAvailable() {
        return propertyRepository.findByIsAvailableIsTrue();
    }

    @Override
    public CustomResponse toggleActiveStatus(@NonNull Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException(Property.class));

        User currentUser = getCurrentUser();

        if (!property.getHost().getId().equals(currentUser.getId())) {
            return new CustomResponse("You are not authorized to modify this property");
        }

        property.setAvailable(!property.isAvailable());
        Property updatedProperty = propertyRepository.save(property);

        String statusMessage = updatedProperty.isAvailable() ? "activated" : "deactivated";

        return new CustomResponse("Property " + statusMessage + " successfully");
    }

    @Override
    public List<Property> findAvailableProperties(AvailablePropertiesRequest request) {

        if(!(request.getCheckOutDate().isAfter(request.getCheckInDate()))) {
            throw new DataValidationException("The Check-out date cannot be before the Check-in date");
        }

        List<Property> availableProperties = propertyRepository.findAvailableProperties(
                request.getCity(),
                request.getCheckInDate(),
                request.getCheckOutDate(),
                (request.getNumOfAdults() + request.getNumOfChildren()));

        availableProperties = availableProperties.stream().filter(Property::isAvailable).toList();

        return availableProperties;

    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}