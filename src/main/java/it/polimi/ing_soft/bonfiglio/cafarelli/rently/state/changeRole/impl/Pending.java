package it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.changeRole.impl;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChangeRole;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChangeRoleStatus;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.changeRole.ChangeRoleState;

/**
 * Represents the state of a change role request that is pending.
 * <p>
 * This class extends the ChangeRoleState class and sets the status to PENDING.
 * It overrides the methods to handle the pending state.
 */

public class Pending extends ChangeRoleState {

    /**
     * Constructor for Pending state.
     * <p>
     * Sets the status to PENDING.
     */
    public Pending() {
        super(ChangeRoleStatus.PENDING);
    }

    @Override
    public void pending(ChangeRole changeRole) {
        throw new IllegalStateException("Change role request is already pending.");
    }

    @Override
    public void accept(ChangeRole changeRole) {
        changeRole.setState(new Accepted());
    }

    @Override
    public void reject(ChangeRole changeRole) {
        changeRole.setState(new Rejected());
    }

}
