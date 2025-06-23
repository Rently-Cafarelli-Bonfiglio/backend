package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.AddTicketReplyRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.TicketCreationRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityNotFoundException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.UserUnauthorizedException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.*;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.TicketReplyRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.TicketRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.ticket.impl.Open;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceImplementationTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private TicketReplyRepository ticketReplyRepository;

    @InjectMocks private TicketServiceImplementation ticketService;

    private User client;
    private User moderator;
    private Ticket ticket;
    private TicketReply ticketReply;
    private TicketCreationRequest creationRequest;
    private AddTicketReplyRequest replyRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup client user
        client = new User();
        client.setId(1L);
        client.setUsername("client");
        client.setRole(Role.CLIENT);

        // Setup moderator user
        moderator = new User();
        moderator.setId(2L);
        moderator.setUsername("moderator");
        moderator.setRole(Role.MODERATOR);

        // Setup ticket
        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTitle("Test Ticket");
        ticket.setDescription("Test Description");
        ticket.setUser(client);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreationDate(LocalDateTime.now());

        // Inizializzazione dello stato del ticket
        ticket.setState(new Open());

        // Setup ticket reply
        ticketReply = new TicketReply();
        ticketReply.setId(1L);
        ticketReply.setContent("Test Reply");
        ticketReply.setUser(moderator);
        ticketReply.setTicket(ticket);
        ticketReply.setFromModerator(true);
        ticketReply.setCreationDate(LocalDateTime.now());

        // Setup creation request
        creationRequest = new TicketCreationRequest("New Ticket", "New Description");

        // Setup reply request
        replyRequest = new AddTicketReplyRequest();
        replyRequest.setContent("New Reply");

        // Set client as the authenticated user by default
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(client, null)
        );
    }

    @Test
    void createTicket_success() {
        // Arrange
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket savedTicket = invocation.getArgument(0);
            savedTicket.setId(1L);
            return savedTicket;
        });

        // Act
        Ticket result = ticketService.createTicket(creationRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Ticket", result.getTitle());
        assertEquals("New Description", result.getDescription());
        assertEquals(client, result.getUser());
        assertEquals(TicketStatus.OPEN, result.getStatus());
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void getTicketById_success() {
        // Arrange
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        // Act
        Ticket result = ticketService.getTicketById(1L);

        // Assert
        assertEquals(ticket, result);
    }

    @Test
    void getTicketById_notFound_throwsException() {
        // Arrange
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            ticketService.getTicketById(999L)
        );
    }

    @Test
    void getTicketsByUser_success() {
        // Arrange
        List<Ticket> tickets = Arrays.asList(ticket);
        when(ticketRepository.findByUserId(1L)).thenReturn(tickets);

        // Act
        List<Ticket> result = ticketService.getTicketsByUser(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(ticket, result.get(0));
    }

    @Test
    void getAllTickets_success() {
        // Arrange
        List<Ticket> tickets = Arrays.asList(ticket);
        when(ticketRepository.findAll()).thenReturn(tickets);

        // Act
        List<Ticket> result = ticketService.getAllTickets();

        // Assert
        assertEquals(1, result.size());
        assertEquals(ticket, result.get(0));
    }

    @Test
    void getTicketsByStatus_success() {
        // Arrange
        List<Ticket> tickets = Arrays.asList(ticket);
        when(ticketRepository.findByStatus(TicketStatus.OPEN)).thenReturn(tickets);

        // Act
        List<Ticket> result = ticketService.getTicketsByStatus(TicketStatus.OPEN);

        // Assert
        assertEquals(1, result.size());
        assertEquals(ticket, result.get(0));
    }

    @Test
    void assignTicketToModerator_success() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(moderator, null)
        );

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        // Act
        Ticket result = ticketService.assignTicketToModerator(1L);

        // Assert
        assertEquals(TicketStatus.IN_PROGRESS, result.getStatus());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void markAsSolved_success() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(moderator, null)
        );

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        // Act
        Ticket result = ticketService.markAsSolved(1L);

        // Assert
        assertEquals(TicketStatus.SOLVED, result.getStatus());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void closeTicket_success() {
        // Arrange
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        // Act
        Ticket result = ticketService.closeTicket(1L);

        // Assert
        assertEquals(TicketStatus.CLOSED, result.getStatus());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void closeTicket_notTicketOwner_throwsException() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(3L);

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(otherUser, null)
        );

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        // Act & Assert
        assertThrows(UserUnauthorizedException.class, () -> 
            ticketService.closeTicket(1L)
        );
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void addReply_asClient_success() {
        // Arrange
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketReplyRepository.save(any(TicketReply.class))).thenAnswer(invocation -> {
            TicketReply savedReply = invocation.getArgument(0);
            savedReply.setId(2L);
            return savedReply;
        });

        // Act
        TicketReply result = ticketService.addReply(replyRequest, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("New Reply", result.getContent());
        assertEquals(client, result.getUser());
        assertEquals(ticket, result.getTicket());
        assertFalse(result.isFromModerator());
        verify(ticketReplyRepository).save(any(TicketReply.class));
    }

    @Test
    void addReply_asModerator_success() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(moderator, null)
        );

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketReplyRepository.save(any(TicketReply.class))).thenAnswer(invocation -> {
            TicketReply savedReply = invocation.getArgument(0);
            savedReply.setId(2L);
            return savedReply;
        });

        // Act
        TicketReply result = ticketService.addReply(replyRequest, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("New Reply", result.getContent());
        assertEquals(moderator, result.getUser());
        assertEquals(ticket, result.getTicket());
        assertTrue(result.isFromModerator());
        verify(ticketReplyRepository).save(any(TicketReply.class));
    }

    @Test
    void addReply_closedTicket_throwsException() {
        // Arrange
        ticket.setStatus(TicketStatus.CLOSED);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> 
            ticketService.addReply(replyRequest, 1L)
        );
        verify(ticketReplyRepository, never()).save(any(TicketReply.class));
    }

    @Test
    void getTicketReplies_success() {
        // Arrange
        List<TicketReply> replies = Arrays.asList(ticketReply);
        when(ticketRepository.existsById(1L)).thenReturn(true);
        when(ticketReplyRepository.findByTicketIdOrderByCreationDate(1L)).thenReturn(replies);

        // Act
        List<TicketReply> result = ticketService.getTicketReplies(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(ticketReply, result.get(0));
    }

    @Test
    void getTicketReplies_ticketNotFound_throwsException() {
        // Arrange
        when(ticketRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            ticketService.getTicketReplies(999L)
        );
    }
}
