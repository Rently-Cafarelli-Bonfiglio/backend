package it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.TicketReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketReplyRepository extends JpaRepository<TicketReply, Long> {

    /**
     * Finds all replies associated with a specific ticket, ordered by creation date.
     *
     * @param ticketId the ID of the ticket
     * @return a list of TicketReply objects associated with the specified ticket, ordered by creation date
     */

    List<TicketReply> findByTicketIdOrderByCreationDate(Long ticketId);
}