package it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.ticket;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Ticket;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.TicketStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * Abstract class representing the state of a ticket.
 * <p>
 * This class defines the common behavior for different states of a ticket.
 * It provides methods to handle transitions between states such as open, in progress, solved, and closed.
 */
@Getter
@Setter
public abstract class TicketState {

    /**
     * The current status of the ticket.
     */
    private TicketStatus status;

    /**
     * Constructor for TicketState.
     *
     * @param status the status of the ticket
     */

    public TicketState(TicketStatus status) {this.status = status; }

    /**
     * Handles the transition to the OPEN state.
     *
     * @param ticket the current ticket
     */

    public abstract void open(Ticket ticket);

    /**
     * Handles the transition to the IN_PROGRESS state.
     *
     * @param ticket the current ticket
     */

    public abstract void inProgress(Ticket ticket);

    /**
     * Handles the transition to the SOLVED state.
     *
     * @param ticket the current ticket
     */

    public abstract void solved(Ticket ticket);

    /**
     * Handles the transition to the CLOSED state.
     *
     * @param ticket the current ticket
     */

    public abstract void closed(Ticket ticket);
}