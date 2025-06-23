package it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.ticket.impl;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Ticket;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.TicketStatus;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.ticket.TicketState;

import java.time.LocalDateTime;

/**
 * Represents the state of a change role request that is pending.
 * <p>
 * This class extends the ChangeRoleState class and sets the status to PENDING.
 * It overrides the methods to handle the pending state.
 */
public class Solved extends TicketState {
    /**
     * Constructor for Pending state.
     * <p>
     * Sets the status to PENDING.
     */
    public Solved() {
        super(TicketStatus.SOLVED);
    }

    @Override
    public void open(Ticket ticket) {
        throw new IllegalStateException("The Ticket is already solved");
    }

    @Override
    public void inProgress (Ticket ticket) {
        ticket.setState(new InProgress());
    }

    @Override
    public void solved (Ticket ticket) {
        throw new IllegalStateException("The Ticket is already solved");
    }

    @Override
    public void closed (Ticket ticket) {
        ticket.setState(new Closed());
        ticket.setClosingDate(LocalDateTime.now());
    }

}
