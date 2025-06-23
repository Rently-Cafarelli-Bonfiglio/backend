package it.polimi.ing_soft.bonfiglio.cafarelli.rently.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.AddTicketReplyRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.TicketCreationRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.TicketDetailResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Ticket;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.TicketReply;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.TicketStatus;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.TicketService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.util.ApiPathUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing support ticket operations.
 * <p>
 * This controller provides endpoints for creating, retrieving, and managing support tickets.
 * It handles ticket creation, assignment, status updates, and replies for both users and moderators.
 * </p>
 */
@RestController
@RequestMapping(ApiPathUtil.REST_PATH + "/ticket")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "API for support ticket management operations")
public class TicketController {
    private final TicketService ticketService;

    /**
     * Creates a new support ticket.
     * <p>
     * This endpoint allows users to create a new support ticket with a title,
     * description, and category to request assistance.
     * </p>
     *
     * @param request DTO containing ticket creation details
     * @return ResponseEntity containing the created ticket
     */
    @Operation(
        summary = "Create a new support ticket",
        description = "Creates a new ticket with the provided details"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Ticket successfully created",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Ticket.class)
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
        )
    })
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    public ResponseEntity<Ticket> createTicket(
            @Parameter(description = "Ticket details for creation", required = true)
            @Valid @RequestBody TicketCreationRequest request) {
        Ticket ticket = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }

    /**
     * Retrieves a specific ticket by its ID.
     * <p>
     * This endpoint returns detailed information about a specific ticket,
     * including its status, description, and all associated replies.
     * </p>
     *
     * @param id the ID of the ticket to retrieve
     * @return ResponseEntity containing the ticket details and replies
     */
    @Operation(
        summary = "Get ticket by ID",
        description = "Retrieves detailed information about a specific ticket and its replies"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ticket found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TicketDetailResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Ticket not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content
        )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT', 'ROLE_MODERATOR')")
    public ResponseEntity<TicketDetailResponse> getTicket(
            @Parameter(description = "ID of the ticket to retrieve", required = true)
            @PathVariable Long id) {
        Ticket ticket = ticketService.getTicketById(id);
        List<TicketReply> replies = ticketService.getTicketReplies(id);
        return ResponseEntity.ok(TicketDetailResponse.fromEntity(ticket, replies));
    }

    /**
     * Retrieves all tickets created by the current user.
     * <p>
     * This endpoint returns a list of all support tickets that were created
     * by the currently authenticated user.
     * </p>
     *
     * @param userDetails the authenticated user's details
     * @return ResponseEntity containing a list of the user's tickets
     */
    @Operation(
        summary = "Get current user's tickets",
        description = "Retrieves all support tickets created by the currently authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of tickets retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Ticket.class))
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content
        )
    })
    @GetMapping("/my-tickets")
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    public ResponseEntity<List<Ticket>> getMyTickets(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((User) userDetails).getId();
        List<Ticket> tickets = ticketService.getTicketsByUser(userId);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Retrieves all tickets in the system, optionally filtered by status.
     * <p>
     * This endpoint returns a list of all support tickets in the system.
     * It can be filtered by status if the status parameter is provided.
     * This endpoint is typically accessible only to moderators.
     * </p>
     *
     * @param status optional filter for ticket status
     * @return ResponseEntity containing a list of tickets
     */
    @Operation(
        summary = "Get all tickets",
        description = "Retrieves all tickets in the system, optionally filtered by status (moderator only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of tickets retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Ticket.class))
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - User is not a moderator",
            content = @Content
        )
    })
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_MODERATOR')")
    public ResponseEntity<List<Ticket>> getAllTickets(
            @Parameter(description = "Filter tickets by status (e.g., OPEN, IN_PROGRESS, SOLVED, CLOSED)")
            @RequestParam(required = false) String status
            ) {
        List<Ticket> tickets;

        if (status != null) {
            tickets = ticketService.getTicketsByStatus(TicketStatus.valueOf(status.toUpperCase()));
        } else {
            tickets = ticketService.getAllTickets();
        }

        return ResponseEntity.ok(tickets);
    }

    /**
     * Assigns a ticket to a moderator.
     * <p>
     * This endpoint allows a moderator to assign a ticket to themselves
     * for handling. The ticket status will be updated to IN_PROGRESS.
     * </p>
     *
     * @param id the ID of the ticket to assign
     * @return ResponseEntity containing the updated ticket
     */
    @Operation(
        summary = "Assign ticket to moderator",
        description = "Assigns a ticket to the current moderator and updates its status to IN_PROGRESS"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ticket successfully assigned",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Ticket.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - User is not a moderator",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Ticket not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Ticket cannot be assigned (e.g., already closed)",
            content = @Content
        )
    })
    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('ROLE_MODERATOR')")
    public ResponseEntity<Ticket> assignTicket(
            @Parameter(description = "ID of the ticket to assign", required = true)
            @PathVariable Long id
        ) {
        Ticket ticket = ticketService.assignTicketToModerator(id);
        return ResponseEntity.ok(ticket);
    }

    /**
     * Marks a ticket as solved.
     * <p>
     * This endpoint allows a moderator to mark a ticket as solved after
     * addressing the user's issue. The ticket status will be updated to SOLVED.
     * </p>
     *
     * @param id the ID of the ticket to mark as solved
     * @return ResponseEntity containing the updated ticket
     */
    @Operation(
        summary = "Mark ticket as solved",
        description = "Updates a ticket's status to SOLVED after the issue has been addressed"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ticket successfully marked as solved",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Ticket.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - User is not a moderator",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Ticket not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Ticket cannot be marked as solved (e.g., already closed)",
            content = @Content
        )
    })
    @PostMapping("/{id}/solve")
    @PreAuthorize("hasAuthority('ROLE_MODERATOR')")
    public ResponseEntity<Ticket> solveTicket(
            @Parameter(description = "ID of the ticket to mark as solved", required = true)
            @PathVariable Long id
            ) {
        Ticket ticket = ticketService.markAsSolved(id);
        return ResponseEntity.ok(ticket);
    }

    /**
     * Closes a ticket.
     * <p>
     * This endpoint allows a user to close their ticket after the issue
     * has been resolved. The ticket status will be updated to CLOSED.
     * </p>
     *
     * @param id the ID of the ticket to close
     * @return ResponseEntity containing the updated ticket
     */
    @Operation(
        summary = "Close a ticket",
        description = "Updates a ticket's status to CLOSED, indicating the issue is resolved"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ticket successfully closed",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Ticket.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - User is not authorized to close this ticket",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Ticket not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Ticket cannot be closed (e.g., already closed)",
            content = @Content
        )
    })
    @PostMapping("/{id}/close")
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    public ResponseEntity<Ticket> closeTicket(
            @Parameter(description = "ID of the ticket to close", required = true)
            @PathVariable Long id
        ){
        Ticket ticket = ticketService.closeTicket(id);
        return ResponseEntity.ok(ticket);
    }

    /**
     * Adds a reply to a ticket.
     * <p>
     * This endpoint allows users and moderators to add replies to a ticket,
     * facilitating communication about the issue.
     * </p>
     *
     * @param id the ID of the ticket to add a reply to
     * @param request DTO containing the reply details
     * @return ResponseEntity containing the created reply
     */
    @Operation(
        summary = "Add reply to a ticket",
        description = "Adds a new reply to an existing ticket to facilitate communication"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Reply successfully added",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TicketReply.class)
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
            description = "Forbidden - User is not authorized to reply to this ticket",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Ticket not found",
            content = @Content
        )
    })
    @PostMapping("/{id}/replies")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT', 'ROLE_MODERATOR')")
    public ResponseEntity<TicketReply> addReply(
            @Parameter(description = "ID of the ticket to add a reply to", required = true)
            @PathVariable Long id,
            @Parameter(description = "Reply details", required = true)
            @Valid @RequestBody AddTicketReplyRequest request
        ){
        TicketReply reply = ticketService.addReply(request, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(reply);
    }
}
