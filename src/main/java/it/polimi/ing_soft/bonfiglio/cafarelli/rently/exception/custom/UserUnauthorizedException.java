package it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom;

/**
 * Custom exception class for user unauthorized errors.
 * This exception is thrown when a user is not authorized to perform an action.
 */
public class UserUnauthorizedException extends RuntimeException {
    public UserUnauthorizedException(String message) {
        super(message);
    }
}
