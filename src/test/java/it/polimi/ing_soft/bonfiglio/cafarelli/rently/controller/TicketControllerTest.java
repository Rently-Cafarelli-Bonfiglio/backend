// Test class for TicketController
package it.polimi.ing_soft.bonfiglio.cafarelli.rently.controller;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.AddTicketReplyRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.TicketCreationRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.TicketDetailResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Ticket;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.TicketReply;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.TicketStatus;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TicketController ticketController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    void createTicket_ShouldReturnCreatedTicket() {
        TicketCreationRequest request = new TicketCreationRequest("Issue Title", "Issue Description");
        when(authentication.getName()).thenReturn("testuser");
        Ticket ticket = new Ticket();
        when(ticketService.createTicket(request)).thenReturn(ticket);

        ResponseEntity<Ticket> response = ticketController.createTicket(request);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(ticket, response.getBody());
    }

    @Test
    void getTicket_ShouldReturnTicketDetailResponse() {
        Long ticketId = 1L;

        // Crea un ticket con tutti i campi necessari
        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.OPEN); // Imposta lo stato per evitare NullPointerException

        List<TicketReply> replies = List.of(new TicketReply());

        when(ticketService.getTicketById(ticketId)).thenReturn(ticket);
        when(ticketService.getTicketReplies(ticketId)).thenReturn(replies);

        ResponseEntity<TicketDetailResponse> response = ticketController.getTicket(ticketId);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(replies, response.getBody().getReplies());
    }

    @Test
    void getMyTickets_ShouldReturnUserTickets() {
        // Corretto: usa Mockito.mock invece di mock
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn(1L);

        List<Ticket> tickets = List.of(new Ticket());
        when(ticketService.getTicketsByUser(1L)).thenReturn(tickets);

        ResponseEntity<List<Ticket>> response = ticketController.getMyTickets(user);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(tickets, response.getBody());
    }

    @Test
    void getAllTickets_ShouldReturnAllTickets() {
        List<Ticket> tickets = List.of(new Ticket());
        when(ticketService.getAllTickets()).thenReturn(tickets);

        ResponseEntity<List<Ticket>> response = ticketController.getAllTickets(null);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(tickets, response.getBody());
    }

    @Test
    void getAllTickets_WithStatus_ShouldReturnFilteredTickets() {
        List<Ticket> tickets = List.of(new Ticket());
        when(ticketService.getTicketsByStatus(TicketStatus.OPEN)).thenReturn(tickets);

        ResponseEntity<List<Ticket>> response = ticketController.getAllTickets("OPEN");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(tickets, response.getBody());
    }

    @Test
    void assignTicket_ShouldReturnUpdatedTicket() {
        Long ticketId = 1L;
        Ticket ticket = new Ticket();
        when(ticketService.assignTicketToModerator(ticketId)).thenReturn(ticket);

        ResponseEntity<Ticket> response = ticketController.assignTicket(ticketId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(ticket, response.getBody());
    }

    @Test
    void solveTicket_ShouldReturnSolvedTicket() {
        Long ticketId = 1L;
        Ticket ticket = new Ticket();
        when(ticketService.markAsSolved(ticketId)).thenReturn(ticket);

        ResponseEntity<Ticket> response = ticketController.solveTicket(ticketId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(ticket, response.getBody());
    }

    @Test
    void closeTicket_ShouldReturnClosedTicket() {
        Long ticketId = 1L;
        Ticket ticket = new Ticket();
        when(ticketService.closeTicket(ticketId)).thenReturn(ticket);

        ResponseEntity<Ticket> response = ticketController.closeTicket(ticketId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(ticket, response.getBody());
    }

    @Test
    void addReply_ShouldReturnCreatedReply() {
        Long ticketId = 1L;
        AddTicketReplyRequest request = new AddTicketReplyRequest();
        TicketReply reply = new TicketReply();

        when(ticketService.addReply(request, ticketId)).thenReturn(reply);

        ResponseEntity<TicketReply> response = ticketController.addReply(ticketId, request);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(reply, response.getBody());
    }
}