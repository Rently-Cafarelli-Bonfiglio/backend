package it.polimi.ing_soft.bonfiglio.cafarelli.rently.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.ChangeRoleRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.ChangeRoleResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.ChangeRoleService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.util.ApiPathUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling role change requests.
 * This controller provides endpoints for requesting a role change, and for admins to accept or reject these requests.
 */
@RestController
@RequestMapping(ApiPathUtil.REST_PATH + "/change-role")
@AllArgsConstructor
@Tag(name = "Role Changes", description = "API for managing role change requests")
public class ChangeRoleController {

    private final ChangeRoleService changeRoleService;

    /**
     * Request a change of role from CLIENT to HOST.
     *
     * @param changeRoleRequest the request containing the motivation for the role change
     * @return a response entity with a message indicating the result of the operation
     */
    @Operation(
        summary = "Request role change",
        description = "Allows a CLIENT to request a role change to HOST"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Role change request submitted successfully",
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
            description = "Unauthorized - User not authenticated",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - User is not a CLIENT",
            content = @Content
        )
    })
    @PostMapping("/request")
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    public ResponseEntity<CustomResponse> requestChangeRole(
        @Parameter(description = "Role change request details", required = true)
        @Valid @RequestBody ChangeRoleRequest changeRoleRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getName();
        CustomResponse response = changeRoleService.requestChangeRole(changeRoleRequest.getMotivation());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all role change requests. Only accessible to ADMIN users.
     *
     * @return a response entity with a list of all role change requests
     */
    @Operation(
        summary = "Get all role change requests",
        description = "Retrieves all pending role change requests for admin review"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of role change requests retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ChangeRoleResponse.class))
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - User is not an admin",
            content = @Content
        )
    })
    @GetMapping("/requests")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ChangeRoleResponse>> findAllChangeRoleRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getName();
        List<ChangeRoleResponse> response = changeRoleService.findAll();
        return ResponseEntity.ok(response);
    }

    /**
     * Accept a role change request. Only accessible to ADMIN users.
     *
     * @param requestId the ID of the role change request to accept
     * @return a response entity with a message indicating the result of the operation
     */
    @Operation(
        summary = "Accept role change request",
        description = "Allows an ADMIN to approve a role change request from CLIENT to HOST"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Role change request accepted successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CustomResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - User is not an ADMIN",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Request not found",
            content = @Content
        )
    })
    @PostMapping("/accept/{requestId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CustomResponse> acceptChangeRole(
        @Parameter(description = "ID of the role change request to accept", required = true)
        @PathVariable Long requestId) {
        CustomResponse response = changeRoleService.acceptChangeRole(requestId);
        return ResponseEntity.ok(response);
    }

    /**
     * Reject a role change request with a motivation. Only accessible to ADMIN users.
     *
     * @param requestId the ID of the role change request to reject
     * @param changeRoleRequest the request containing the motivation for rejecting the request
     * @return a response entity with a message indicating the result of the operation
     */
    @Operation(
        summary = "Reject role change request",
        description = "Allows an ADMIN to reject a role change request with a motivation"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Role change request rejected successfully",
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
            description = "Unauthorized - User not authenticated",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - User is not an ADMIN",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Request not found",
            content = @Content
        )
    })
    @PostMapping("reject/{requestId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CustomResponse> rejectChangeRole(
        @Parameter(description = "ID of the role change request to reject", required = true)
        @PathVariable Long requestId,

        @Parameter(description = "Rejection details including motivation", required = true)
        @Valid @RequestBody ChangeRoleRequest changeRoleRequest) {
        CustomResponse response = changeRoleService.rejectChangeRole(requestId, changeRoleRequest.getMotivation());
        return ResponseEntity.ok(response);
    }
}
