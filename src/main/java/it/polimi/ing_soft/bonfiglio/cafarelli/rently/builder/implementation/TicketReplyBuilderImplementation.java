package it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.implementation;


import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition.TicketReplyBuilder;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Ticket;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.TicketReply;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;

/**
 * Implementation of the {@link TicketReplyBuilder} interface for building {@link TicketReply} objects.
 */
// This class uses the Builder design pattern to create instances of Booking.
public class TicketReplyBuilderImplementation implements TicketReplyBuilder {
    private Long id;
    private String content;
    private Ticket ticket;
    private User user;
    private boolean fromModerator;

    @Override
    public TicketReplyBuilder id(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public TicketReplyBuilder content(String content) {
        this.content = content;
        return this;
    }

    @Override
    public TicketReplyBuilder ticket(Ticket ticket) {
        this.ticket = ticket;
        return this;
    }

    @Override
    public TicketReplyBuilder user(User user) {
        this.user = user;
        return this;
    }

    @Override
    public TicketReplyBuilder fromModerator(boolean fromModerator) {
        this.fromModerator = fromModerator;
        return this;
    }

    @Override
    public TicketReply build() {
        return new TicketReply(id, content, null, ticket, user, fromModerator);
    }
}
