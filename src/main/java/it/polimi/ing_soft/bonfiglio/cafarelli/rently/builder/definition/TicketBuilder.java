package it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Ticket;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.TicketStatus;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.ticket.TicketState;

/**
 * Interface for building {@link Ticket} objects.
 */
public interface TicketBuilder {

    TicketBuilder ticketId(Long ticketId);

    TicketBuilder title(String title);

    TicketBuilder description(String description);

    TicketBuilder userId(User userId);

    TicketBuilder state(TicketState state);

    TicketBuilder status(TicketStatus status);

    Ticket build();
}
