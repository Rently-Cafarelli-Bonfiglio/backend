package it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing User entities.
 * It extends JpaRepository to provide CRUD operations and custom query methods.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a User by their username.
     *
     * @param username the username of the user
     * @return an Optional containing the User if found, or empty if not found
     */

    Optional<User> findByUsername(String username);

    /**
     * Finds a User by their email.
     *
     * @param email the email of the user
     * @return an Optional containing the User if found, or empty if not found
     */

    Optional<User> findByEmail(String email);

    /**
     * Finds a User by their username or email.
     *
     * @param username the username of the user
     * @param email the email of the user
     * @return an Optional containing the User if found, or empty if not found
     */

    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * @NonNull annotation is used to avoid null values, instead an empty list is returned
     *
     * @return all users
     */

    @NonNull
    List<User> findAll();

    /**
     * Finds users by their username.
     *
     * @param username the username to search for
     * @return a list of User objects matching the given username
     */

    List<User> username(String username);
}
