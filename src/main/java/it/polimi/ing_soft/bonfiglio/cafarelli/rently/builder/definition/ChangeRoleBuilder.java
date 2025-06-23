package it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChangeRole;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChangeRoleStatus;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.changeRole.ChangeRoleState;

/**
 * Interface for building {@link ChangeRole} objects.
 */
public interface ChangeRoleBuilder {

    ChangeRoleBuilder id(Long id);

    ChangeRoleBuilder user(User user);

    ChangeRoleBuilder state(ChangeRoleState state);

    ChangeRoleBuilder status(ChangeRoleStatus status);

    ChangeRoleBuilder fullfilledBy(User fullfilledBy);

    ChangeRoleBuilder motivation(String motivation);

    ChangeRole build();
}

