package it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.changeRole.impl;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChangeRole;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChangeRoleStatus;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.changeRole.ChangeRoleState;

/**
 * Represents the state of a change role request that has been accepted.
 * <p>
 * This class extends the ChangeRoleState class and sets the status to ACCEPTED.
 * It overrides the methods to handle the accepted state.
 */

public class Accepted extends ChangeRoleState {
    /**
     * Constructor for Accepted state.
     * <p>
     * Sets the status to ACCEPTED.
     */
    public Accepted() {
        super(ChangeRoleStatus.ACCEPTED);
    }

    @Override
    public void pending(ChangeRole changeRole) {
        throw new IllegalStateException("Change role request is already accepted.");
    }

    @Override
    public void accept(ChangeRole changeRole) {
        throw new IllegalStateException("Change role request is already accepted.");
    }

    @Override
    public void reject(ChangeRole changeRole) {
        throw new IllegalStateException("Change role request cannot be rejected after acceptance.");
    }
}
