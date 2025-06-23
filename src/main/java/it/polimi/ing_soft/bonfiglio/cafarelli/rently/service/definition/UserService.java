package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition;


import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.UserModifyRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.UserPasswordChangeRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

/**
 * This interface defines the contract for user services.
 * It includes methods for modifying user details, changing passwords,
 * enabling/disabling users, and managing favorite properties.
 */
public interface UserService {

    /**
     * Modifies user details based on the provided request.
     *
     * @param request the request containing user modification details
     * @return a CustomResponse indicating the result of the operation
     */

    CustomResponse modify(@NonNull UserModifyRequest request);

    /**
     * Modifies user details based on the provided request.
     *
     * @param request the request containing user modification details
     * @return a CustomResponse indicating the result of the operation
     */

    /**
     * Modifies user details with an image based on the provided request.
     *
     * @param request the request containing user modification details
     * @param file the image file to be associated with the user
     * @return a CustomResponse indicating the result of the operation
     */

    CustomResponse modifyWithImage(@NonNull UserModifyRequest request, MultipartFile file);

    /**
     * Changes the password for a user based on the provided request.
     *
     * @param request the request containing user password change details
     * @return a CustomResponse indicating the result of the operation
     */

    CustomResponse changePassword(@NonNull UserPasswordChangeRequest request);

    /**
     * Disables a user by their ID.
     *
     * @param userId the ID of the user to be disabled
     * @return a CustomResponse indicating the result of the operation
     */

    CustomResponse disable(@NonNull Long userId);

    /**
     * Enables a user by their ID.
     *
     * @param userId the ID of the user to be enabled
     * @return a CustomResponse indicating the result of the operation
     */

    CustomResponse enable(@NonNull Long userId);

    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */

    List<User> findAll();

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user to be found
     * @return the User object if found, null otherwise
     */

    User findByUsername(String username);

    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user to be found
     * @return the User object if found, null otherwise
     */

    User findById(Long id);

    /**
     * Retrieves the currently logged-in user.
     *
     * @return the currently logged-in User object
     */

    User getCurrentUser();

    /**
     * Adds a property to the user's favorites.
     *
     * @param username the username of the user
     * @param propertyId the ID of the property to be added to favorites
     * @return the updated User object after adding the favorite property
     */

    User addFavoriteProperty(String username, Long propertyId);

    /**
     * Removes a property from the user's favorites.
     *
     * @param username the username of the user
     * @param propertyId the ID of the property to be removed from favorites
     * @return the updated User object after removing the favorite property
     */

    User removeFavoriteProperty(String username, Long propertyId);

    /**
     * Retrieves all favorite properties of a user.
     *
     * @param username the username of the user
     * @return a list of all favorite properties for the user
     */

    List<Property> getAllFavoriteProperties(String username);

    /**
     * Recharges the user's balance.
     *
     * @param username the username of the user
     * @param amount the amount to be recharged
     * @return the updated User object after recharging the balance
     */

    User rechargeBalance(String username, BigDecimal amount);

    /**
     * Deducts an amount from the user's balance.
     *
     * @param username the username of the user
     * @param amount the amount to be deducted
     * @return true if the deduction was successful, false otherwise
     */

    boolean deductBalance(String username, BigDecimal amount);
}
