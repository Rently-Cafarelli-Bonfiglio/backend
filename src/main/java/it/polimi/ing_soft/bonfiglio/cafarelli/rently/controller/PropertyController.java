package it.polimi.ing_soft.bonfiglio.cafarelli.rently.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.AvailablePropertiesRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.PropertyCreateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.PropertyUpdateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.PropertyService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.util.ApiPathUtil;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Controller for managing property-related operations.
 * <p>
 * This controller provides endpoints for creating, updating, deleting, and retrieving
 * property listings. It handles all property management operations for hosts and provides
 * search capabilities for potential renters.
 * </p>
 */
@RestController
@RequestMapping(ApiPathUtil.REST_PATH + "/property")
@AllArgsConstructor
@Tag(name = "Properties", description = "API for property management operations")
public class PropertyController {
    /**
     * Service responsible for handling business logic related to properties.
     */
    private final PropertyService propertyService;

   private final Validator validator;
    /**
     * Creates a new property listing.
     * <p>
     * This endpoint allows hosts to create a new property with details and images.
     * </p>
     *
     * @param propertyCreateRequestJson JSON string containing property creation details
     * @param images List of images associated with the property
     * @return ResponseEntity with a custom response indicating the result of the operation
     * @throws IOException if there is an error reading the property creation request
     */
    @Operation(
        summary = "Create a new property listing",
        description = "Creates a new property with the provided details and images"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Property successfully created",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CustomResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated or not a host",
            content = @Content
        )
    })
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_HOST')")
    public ResponseEntity<CustomResponse> createProperty(
        @Parameter(description = "Property details for creation", required = true)
        @Valid @RequestPart String propertyCreateRequestJson,

        @Parameter(description = "Property images", required = true)
        @RequestPart List<MultipartFile> images) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        PropertyCreateRequest propertyCreateRequest = objectMapper.readValue(
                propertyCreateRequestJson,
                PropertyCreateRequest.class);

        var violations = validator.validate(propertyCreateRequest);
        if(!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        final CustomResponse response = propertyService.createProperty(propertyCreateRequest, images);
        return ResponseEntity
                .ok(response);
    }

    /**
     * Updates an existing property listing.
     * <p>
     * This endpoint allows hosts to modify details of their existing property listings.
     * </p>
     *
     * @param propertyUpdateRequestJson DTO containing property update details
     * @return ResponseEntity with a custom response indicating the result of the operation
     */
    @Operation(
        summary = "Update an existing property",
        description = "Updates property details and optionally adds new images"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Property successfully updated",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CustomResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated or not the property owner",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Property not found",
            content = @Content
        )
    })
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ROLE_HOST')")
    public ResponseEntity<CustomResponse> updateProperty(
        @Parameter(description = "Property details for update", required = true)
        @Valid @RequestPart String propertyUpdateRequestJson,

        @Parameter(description = "New property images (optional)")
        @RequestPart(required = false) List<MultipartFile> images) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        PropertyUpdateRequest propertyUpdateRequest = objectMapper.readValue(
                propertyUpdateRequestJson,
                PropertyUpdateRequest.class);

        var violations = validator.validate(propertyUpdateRequest);
        if(!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        CustomResponse response;

        if(images == null || images.isEmpty()) {
            response = propertyService.updateProperty(propertyUpdateRequest);
        } else {
            response = propertyService.updateProperty(propertyUpdateRequest, images);
        }

        return ResponseEntity
                .ok(response);
    }


    /**
     * Deletes a property listing.
     * <p>
     * This endpoint allows hosts to delete their property listings.
     * </p>
     *
     * @param propertyId the ID of the property to delete
     * @return ResponseEntity with a custom response indicating the result of the operation
     */
    @Operation(
        summary = "Delete a property",
        description = "Deletes a property listing by its ID")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Property successfully deleted",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CustomResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated or not the property owner",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Property not found",
            content = @Content
        )
    })
    @DeleteMapping("/delete/{propertyId}")
    @PreAuthorize("hasAnyAuthority('ROLE_HOST', 'ROLE_ADMIN', 'ROLE_MODERATOR')")
    public ResponseEntity<CustomResponse> deleteProperty(
        @Parameter(description = "ID of the property to delete", required = true)
        @PathVariable Long propertyId) {
        CustomResponse response = propertyService.deleteProperty(propertyId);
        return ResponseEntity
                .ok(response);
    }
    /**
     * Retrieves a specific property by its ID.
     * <p>
     * This endpoint allows users to get detailed information about a specific property.
     * </p>
     *
     * @param propertyId the ID of the property to retrieve
     * @return ResponseEntity containing the property details
     */
    @Operation(
        summary = "Get property by ID",
        description = "Retrieves detailed information about a specific property"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Property found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Property.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Property not found",
            content = @Content
        )
    })
    @GetMapping("/{propertyId}")
    public ResponseEntity<Property> getPropertyById(
        @Parameter(description = "ID of the property to retrieve", required = true)
        @PathVariable Long propertyId) {
        Property property = propertyService.findById(propertyId);
        return ResponseEntity
                .ok(property);
    }

    /**
     * Retrieves all property listings.
     * <p>
     * This endpoint returns a list of all properties available on the platform.
     * </p>
     *
     * @return ResponseEntity containing a list of all properties
     */
    @Operation(
        summary = "Get all properties",
        description = "Retrieves a list of all properties available on the platform"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of properties retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Property.class))
            )
        )
    })
    @GetMapping
    public ResponseEntity<List<Property>> getAllProperties() {
        List<Property> properties = propertyService.findAll();
        return ResponseEntity
                .ok(properties);
    }

    /**
     * Retrieves all properties owned by a specific host.
     * <p>
     * This endpoint allows filtering properties by their host's ID.
     * </p>
     *
     * @param hostId the ID of the host whose properties to retrieve
     * @return ResponseEntity containing a list of properties owned by the specified host
     */
    @Operation(
        summary = "Get properties by host ID",
        description = "Retrieves all properties owned by a specific host"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of properties retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Property.class))
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Host not found",
            content = @Content
        )
    })
    @GetMapping("/host/{hostId}")
    public ResponseEntity<List<Property>> getPropertiesByHostId(
        @Parameter(description = "ID of the host whose properties to retrieve", required = true)
        @PathVariable Long hostId) {
        List<Property> properties = propertyService.findByHostId(hostId);
        return ResponseEntity
                .ok(properties);
    }

    /**
     * Retrieves all properties in a specific city.
     * <p>
     * This endpoint allows filtering properties by city location.
     * </p>
     *
     * @param city the name of the city to filter properties by
     * @return ResponseEntity containing a list of properties in the specified city
     */
    @Operation(
        summary = "Get properties by city",
        description = "Retrieves all properties located in a specific city"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of properties retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Property.class))
            )
        )
    })
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Property>> getPropertiesByCity(
        @Parameter(description = "Name of the city to filter properties by", required = true)
        @PathVariable String city) {
        List<Property> properties = propertyService.findByCity(city);
        return ResponseEntity
                .ok(properties);
    }

    /**
     * Retrieves all currently available properties.
     * <p>
     * This endpoint returns only properties that are currently available for rent.
     * </p>
     *
     * @return ResponseEntity containing a list of available properties
     */
    @Operation(
        summary = "Get available properties",
        description = "Retrieves all properties that are currently available for rent"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of available properties retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Property.class))
            )
        )
    })
    @GetMapping("/available")
    public ResponseEntity<List<Property>> getAvailableProperties() {
        List<Property> properties = propertyService.findAvailable();
        return ResponseEntity
                .ok(properties);
    }

    /**
     * Toggles the active status of a property.
     * <p>
     * This endpoint allows hosts to change the availability status of their property
     * (making it visible or hidden from search results).
     * </p>
     *
     * @param propertyId the ID of the property to toggle status for
     * @return ResponseEntity with a custom response indicating the result of the operation
     */
    @Operation(
        summary = "Toggle property active status",
        description = "Changes the availability status of a property (visible or hidden from search results)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Property status successfully toggled",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CustomResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated or not the property owner",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Property not found",
            content = @Content
        )
    })
    @PostMapping("/toggle-active/{propertyId}")
    @PreAuthorize("hasAuthority('ROLE_HOST')")
    public ResponseEntity<CustomResponse> toggleActiveStatus(
        @Parameter(description = "ID of the property to toggle status for", required = true)
        @PathVariable Long propertyId) {
        CustomResponse response = propertyService.toggleActiveStatus(propertyId);
        return ResponseEntity
                .ok(response);
    }
    /**
     * Searches for available properties based on specific criteria.
     * <p>
     * This endpoint allows users to search for properties that are available
     * for a specific date range, in a specific city, and can accommodate
     * the specified number of guests.
     * </p>
     *
     * @param request the criteria for finding available properties
     * @return ResponseEntity containing a list of properties that match the criteria
     */
    @Operation(
        summary = "Search for available properties",
        description = "Finds properties that are available for a specific date range, in a specific city, and can accommodate the specified number of guests"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of available properties retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Property.class))
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid search criteria",
            content = @Content
        )
    })
    @PostMapping("/search/available")
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    public ResponseEntity<List<Property>> searchAvailableProperties(
        @Parameter(description = "Criteria for finding available properties", required = true)
        @Valid @RequestBody AvailablePropertiesRequest request) {
        List<Property> properties = propertyService.findAvailableProperties(request);
        return ResponseEntity
                .ok(properties);
    }
}
