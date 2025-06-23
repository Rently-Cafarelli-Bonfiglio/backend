package it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition.ChangeRoleBuilder;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChangeRole;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChangeRoleStatus;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.changeRole.ChangeRoleState;

/**
 * Implementation of the {@link ChangeRoleBuilder} interface for building {@link ChangeRole} objects.
 */
// This class provides methods to set the user and motivation for the change role request.
public class ChangeRoleBuilderImplementation implements ChangeRoleBuilder {
    private Long id;
    private User user;
    private ChangeRoleState state;
    private ChangeRoleStatus status;
    private User fullfilledBy;
    private String motivation;


    @Override
    public ChangeRoleBuilder id(Long id) {
        this.id = id;
        return this;
    }
    @Override
    public ChangeRoleBuilder user(User user) {
        this.user = user;
        return this;
    }

    @Override
    public ChangeRoleBuilder state(ChangeRoleState state) {
        this.state = state;
        return this;
    }

    @Override
    public ChangeRoleBuilder status(ChangeRoleStatus status) {
        this.status = status;
        return this;
    }

    @Override
    public ChangeRoleBuilder fullfilledBy(User fullfilledBy) {
        this.fullfilledBy = fullfilledBy;
        return this;
    }

    @Override
     public ChangeRoleBuilder motivation(String motivation) {
         this.motivation = motivation;
         return this;
     }

    @Override
    public ChangeRole build() {

        return new ChangeRole(
                id,
                user,
                state,
                status,
                null,
                null,
                fullfilledBy,
                motivation
        );
    }
}
