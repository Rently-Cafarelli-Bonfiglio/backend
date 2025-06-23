package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.AddTicketReplyRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.TicketCreationRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Ticket;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.TicketReply;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.TicketStatus;

import java.util.List;

public interface TicketService {

    /**
     * Creates a new ticket.
     *
     * @param request the request containing ticket details
     * @return the created ticket
     */

    Ticket createTicket(TicketCreationRequest request);

    /**
     * Retrieves a ticket by its ID.
     *
     * @param id the ID of the ticket
     * @return the ticket with the specified ID
     */

    Ticket getTicketById(Long id);

    /**
     * Retrieves all tickets created by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of tickets created by the user
     */

    List<Ticket> getTicketsByUser(Long userId);

    /**
     * Retrieves all tickets with a specific status.
     *
     * @return a list of tickets with the specified status
     */

    List<Ticket> getAllTickets();

    List<Ticket> getTicketsByStatus(TicketStatus status);

    /**
     * Assigns a ticket to a moderator.
     *
     * @param ticketId the ID of the ticket to assign
     * @return the updated ticket after assignment
     */

    Ticket assignTicketToModerator(Long ticketId);

    /**
     * Marks a ticket as solved.
     *
     * @param ticketId the ID of the ticket to mark as solved
     * @return the updated ticket after marking it as solved
     */

    Ticket markAsSolved(Long ticketId);

    /**
     * Closes a ticket.
     *
     * @param ticketId the ID of the ticket to close
     * @return the closed ticket
     */

    Ticket closeTicket(Long ticketId);

    /**
     * Adds a reply to a ticket.
     *
     * @param request the request containing the reply details
     * @param ticketId the ID of the ticket to which the reply is added
     * @return the added ticket reply
     */

    TicketReply addReply(AddTicketReplyRequest request, Long ticketId);

    /**
     * Retrieves all replies for a specific ticket.
     *
     * @param ticketId the ID of the ticket
     * @return a list of replies for the specified ticket
     */

    List<TicketReply> getTicketReplies(Long ticketId);
}
