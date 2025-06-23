package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.AvailablePropertiesRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.PropertyCreateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.PropertyUpdateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * This interface defines the contract for property services.
 * It includes methods for creating, updating, deleting, and retrieving properties.
 */

public interface PropertyService {

    /**
     * Creates a new property.
     *
     * @param propertyCreateRequest the request containing property details
     * @param images a list of images associated with the property
     * @return a CustomResponse indicating the result of the operation
     */

    CustomResponse createProperty(@NonNull PropertyCreateRequest propertyCreateRequest, @NonNull List<MultipartFile> images);

    /**
     * Updates an existing property.
     *
     * @param propertyUpdateRequest the request containing updated property details
     * @return a CustomResponse indicating the result of the operation
     */

    CustomResponse updateProperty(@NonNull PropertyUpdateRequest propertyUpdateRequest);

    /**
     * Updates an existing property with images.
     *
     * @param propertyUpdateRequest the request containing updated property details
     * @param images a list of images associated with the property
     * @return a CustomResponse indicating the result of the operation
     */

    CustomResponse updateProperty(@NonNull PropertyUpdateRequest propertyUpdateRequest, List<MultipartFile> images);

    /**
     * Deletes a property by its ID.
     *
     * @param propertyId the ID of the property to be deleted
     * @return a CustomResponse indicating the result of the operation
     */

    CustomResponse deleteProperty(Long propertyId);

    /**
     * Finds a property by its ID.
     *
     * @param propertyId the ID of the property to be found
     * @return the Property object if found, null otherwise
     */

    Property findById(Long propertyId);

    /**
     * Finds all properties.
     *
     * @return a list of all properties
     */

    List<Property> findAll();

    /**
     * Finds properties by host ID.
     *
     * @param hostId the ID of the host whose properties are to be found
     * @return a list of properties associated with the specified host ID
     */

    List<Property> findByHostId(Long hostId);

    /**
     * Finds properties by city.
     *
     * @param city the city in which to search for properties
     * @return a list of properties located in the specified city
     */

    List<Property> findByCity(String city);

    /**
     * Finds all available properties.
     *
     * @return a list of all available properties
     */

    List<Property> findAvailable();

    /**
     * Toggles the active status of a property.
     *
     * @param propertyId the ID of the property whose active status is to be toggled
     * @return a CustomResponse indicating the result of the operation
     */

    CustomResponse toggleActiveStatus(Long propertyId);

    /**
     * Finds available properties based on the provided request.
     *
     * @param request the request containing criteria for finding available properties
     * @return a list of properties that match the criteria in the request
     */

    List <Property> findAvailableProperties(AvailablePropertiesRequest request);

}