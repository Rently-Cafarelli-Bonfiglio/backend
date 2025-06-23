package it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.ticket.impl;


import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Ticket;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.TicketStatus;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.ticket.TicketState;

/**
 * Represents the state of a change role request that is pending.
 * <p>
 * This class extends the ChangeRoleState class and sets the status to PENDING.
 * It overrides the methods to handle the pending state.
 */
public class Open extends TicketState {
    /**
     * Constructor for Pending state.
     * <p>
     * Sets the status to PENDING.
     */
    public Open() {
        super(TicketStatus.OPEN);
    }

    @Override
    public void open (Ticket ticket) {
        throw new IllegalStateException("The Ticket is already open");
    }

    @Override
    public void inProgress(Ticket ticket) {
        ticket.setState(new InProgress());
    }

    @Override
    public void solved(Ticket ticket) {
        ticket.setState(new Solved());
    }

    @Override
    public void closed(Ticket ticket) {
        ticket.setState(new Closed());
    }

}
