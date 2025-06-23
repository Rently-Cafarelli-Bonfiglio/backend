package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.AddTicketReplyRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.TicketCreationRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityNotFoundException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.UserUnauthorizedException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.*;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.TicketReplyRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.TicketRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImplementation implements TicketService {
    private final TicketRepository ticketRepository;
    private final TicketReplyRepository ticketReplyRepository;

    public Ticket createTicket(TicketCreationRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Ticket ticket = new Ticket(request.getTitle(), request.getDescription(), user);
        return ticketRepository.save(ticket);
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Ticket.class));
    }

    public List<Ticket> getTicketsByUser(Long userId) {
        return ticketRepository.findByUserId(userId);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public List<Ticket> getTicketsByStatus(TicketStatus status) {
        return ticketRepository.findByStatus(status);
    }

    public Ticket assignTicketToModerator(Long ticketId) {
        Ticket ticket = getTicketById(ticketId);
        SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ticket.inProgress();
        return ticketRepository.save(ticket);
    }

    public Ticket markAsSolved(Long ticketId) {
        Ticket ticket = getTicketById(ticketId);
       SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ticket.solved();
        return ticketRepository.save(ticket);
    }

    public Ticket closeTicket(Long ticketId) {
        Ticket ticket = getTicketById(ticketId);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Verifica che l'utente sia il proprietario del ticket
        if (!ticket.getUser().getId().equals(user.getId())) {
            throw new UserUnauthorizedException("You are not allowed to close this ticket");
        }

        ticket.closed();
        return ticketRepository.save(ticket);
    }

    public TicketReply addReply(AddTicketReplyRequest request, Long ticketId) {
        Ticket ticket = getTicketById(ticketId);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Verifica che il ticket non sia chiuso
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new IllegalStateException("Non è possibile aggiungere risposte a un ticket chiuso");
        }

        boolean isModerator = user.getRole() == Role.MODERATOR;

        TicketReply reply = new TicketReply();
        reply.setContent(request.getContent());
        reply.setTicket(ticket);
        reply.setUser(user);
        reply.setFromModerator(isModerator);

        return ticketReplyRepository.save(reply);
    }

    public List<TicketReply> getTicketReplies(Long ticketId) {
        // Verifica che il ticket esista
        if (!ticketRepository.existsById(ticketId)) {
            throw new EntityNotFoundException(Ticket.class);
        }

        return ticketReplyRepository.findByTicketIdOrderByCreationDate(ticketId);
    }
}
