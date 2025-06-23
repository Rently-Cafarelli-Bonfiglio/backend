package it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChangeRole;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChangeRoleStatus;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link ChangeRole} entity.
 * This interface provides methods to interact with the database for ChangeRole entities.
 */
@Repository
public interface ChangeRoleRepository extends JpaRepository<ChangeRole, Long> {
    
    /**
     * Find all change role requests by user.
     *
     * @param user the user
     * @return the list of change role requests
     */
    List<ChangeRole> findByUser(User user);
    
    /**
     * Find all change role requests by status.
     *
     * @param status the status
     * @return the list of change role requests
     */
    List<ChangeRole> findByStatus(ChangeRoleStatus status);
    
    /**
     * Find a pending change role request by user.
     *
     * @param user the user
     * @param status the status (PENDING)
     * @return the optional change role request
     */
    Optional<ChangeRole> findByUserAndStatus(User user, ChangeRoleStatus status);

    /**
     * Find all change role requests in pending state.
     *
     * @return the list of change role requests in pending state
     */
    List<ChangeRole>findAll();
}