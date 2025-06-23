package it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom;

/**
 * Custom exception class for user disabled errors.
 * This exception is thrown when a user is disabled and cannot perform certain actions.
 */
public class UserDisabledException extends RuntimeException {

    public UserDisabledException(String message) {
        super(message);
    }
}
