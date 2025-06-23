package it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.changeRole.impl;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChangeRole;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChangeRoleStatus;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.changeRole.ChangeRoleState;


/**
 * Represents the state of a change role request that has been rejected.
 * <p>
 * This class extends the ChangeRoleState class and sets the status to REJECTED.
 * It overrides the methods to handle the rejected state.
 */
public class Rejected extends ChangeRoleState {
    /**
     * Constructor for Rejected state.
     * <p>
     * Sets the status to REJECTED.
     */
    public Rejected() {
        super(ChangeRoleStatus.REJECTED);
    }

    @Override
    public void pending(ChangeRole changeRole) {
        throw new IllegalStateException("Change role request cannot be set to pending after rejection.");
    }

    @Override
    public void accept(ChangeRole changeRole) {
        throw new IllegalStateException("Change role request cannot be accepted after rejection.");
    }

    @Override
    public void reject(ChangeRole changeRole) {
        throw new IllegalStateException("Change role request is already rejected.");
    }
}
