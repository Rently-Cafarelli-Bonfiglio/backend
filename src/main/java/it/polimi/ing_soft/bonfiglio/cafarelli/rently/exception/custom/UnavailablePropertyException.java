package it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom;

/**
 * Custom exception class for unavailable property errors.
 * This exception is thrown when a property is not available.
 */
public class UnavailablePropertyException extends RuntimeException {
    public UnavailablePropertyException(String message) {
        super(message);
    }

    public UnavailablePropertyException(String message, Throwable cause) {
        super(message, cause);
    }

}