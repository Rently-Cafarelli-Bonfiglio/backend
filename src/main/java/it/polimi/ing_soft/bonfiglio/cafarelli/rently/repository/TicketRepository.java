package it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Ticket;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Finds all tickets created by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of tickets created by the specified user
     */

    List<Ticket> findByUserId(Long userId);

    /**
     * Finds all tickets with a specific status.
     *
     * @param status the status of the tickets
     * @return a list of tickets with the specified status
     */

    List<Ticket> findByStatus(TicketStatus status);
}
