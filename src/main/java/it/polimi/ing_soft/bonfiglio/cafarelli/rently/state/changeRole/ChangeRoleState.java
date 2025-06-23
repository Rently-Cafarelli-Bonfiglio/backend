package it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.changeRole;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChangeRole;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChangeRoleStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * Abstract class representing the state of a change role request.
 * <p>
 * This class defines the common behavior for different states of a change role request.
 * It provides methods to handle the pending, accepted, and rejected states.
 */
@Getter
@Setter

public abstract class ChangeRoleState {

    /**
     * The status of the change role request.
     */
    private ChangeRoleStatus status;

    /**
     * Constructor for ChangeRoleState.
     *
     * @param status the status of the change role request
     */

    public ChangeRoleState(ChangeRoleStatus status) {
        this.status = status;
    }

    /**
     * Default constructor for ChangeRoleState.
     */
    public abstract void pending(ChangeRole changeRole);
    /**
     * Method to handle the acceptance of a change role request.
     *
     * @param changeRole the change role request to be accepted
     */

    public abstract void accept(ChangeRole changeRole);

    /**
     * Method to handle the rejection of a change role request.
     *
     * @param changeRole the change role request to be rejected
     */

    public abstract void reject(ChangeRole changeRole);

}

