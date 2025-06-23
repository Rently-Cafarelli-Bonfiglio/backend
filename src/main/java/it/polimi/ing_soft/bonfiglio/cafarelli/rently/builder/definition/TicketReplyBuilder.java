package it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Ticket;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.TicketReply;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;

/**
 * Interface for building {@link TicketReply} objects.
 */
public interface TicketReplyBuilder {

    TicketReplyBuilder id(Long id);

    TicketReplyBuilder content(String content);

    TicketReplyBuilder  ticket(Ticket ticket);

    TicketReplyBuilder  user(User user);

    TicketReplyBuilder  fromModerator(boolean fromModerator);

    TicketReply build();

}
