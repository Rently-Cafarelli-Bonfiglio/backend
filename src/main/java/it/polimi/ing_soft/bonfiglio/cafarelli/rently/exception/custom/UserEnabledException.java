package it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom;

/**
 * Custom exception class for user enabled errors.
 * This exception is thrown when a user is not enabled.
 */
public class UserEnabledException extends RuntimeException {
    public UserEnabledException(String message) {
        super(message);
    }
}
