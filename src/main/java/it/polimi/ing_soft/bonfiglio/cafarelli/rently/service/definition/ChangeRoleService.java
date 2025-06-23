package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.ChangeRoleResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;

import java.util.List;

/**
 * This interface defines the contract for change role services.
 * It includes methods for requesting a change of user role, and for admins to accept or reject these requests.
 */
public interface ChangeRoleService {
    /**
     * Request a change of role from CLIENT to HOST.
     *
     * @param motivation the motivation for the role change request
     * @return a custom response indicating the result of the operation
     */
    CustomResponse requestChangeRole(String motivation);

    /**
     * Accept a role change request.
     *
     * @param requestId the ID of the role change request to accept
     * @return a custom response indicating the result of the operation
     */
    CustomResponse acceptChangeRole(Long requestId);

    /**
     * Reject a role change request with a motivation.
     *
     * @param requestId  the ID of the role change request to reject
     * @param motivation the motivation for rejecting the request
     * @return a custom response indicating the result of the operation
     */
    CustomResponse rejectChangeRole(Long requestId, String motivation);

    /**
     * Find all change role requests.
     *
     * @return a list of ChangeRoleResponse objects representing all change role requests
     */

    List<ChangeRoleResponse> findAll();
}
