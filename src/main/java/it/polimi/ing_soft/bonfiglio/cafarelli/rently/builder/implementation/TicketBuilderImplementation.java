package it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition.TicketBuilder;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Ticket;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.TicketStatus;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.ticket.TicketState;

/**
 * Implementation of the {@link TicketBuilder} interface for building {@link Ticket} objects.
 */
// This class uses the Builder design pattern to create instances of Ticket.
public class TicketBuilderImplementation implements TicketBuilder {
    private Long ticketId;
    private String title;
    private String description;
    private User userId;
    private TicketState state;
    private TicketStatus status;

    @Override
    public TicketBuilder ticketId(Long ticketId) {
        this.ticketId = ticketId;
        return this;
    }

    @Override
    public TicketBuilder title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public TicketBuilder description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public TicketBuilder userId(User userId) {
        this.userId = userId;
        return this;
    }


    @Override
    public TicketBuilder state(TicketState state) {
        this.state = state;
        return this;
    }

    @Override
    public TicketBuilder status(TicketStatus status) {
        this.status = status;
        return this;
    }

    @Override
    public Ticket build(){ return new Ticket(ticketId, title, description, null, null, userId, state, status); }

}
