package it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom;

/**
 * Custom exception class for data validation errors.
 * This exception is thrown when data validation fails.
 */
public class DataValidationException extends RuntimeException {
    public DataValidationException(String message) {

        super(message);
    }

    public DataValidationException()
    {

        super("Data validation failed.");
    }
}
