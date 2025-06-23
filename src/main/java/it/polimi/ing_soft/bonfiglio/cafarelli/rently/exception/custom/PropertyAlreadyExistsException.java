package it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom;

public class PropertyAlreadyExistsException extends RuntimeException {
    public PropertyAlreadyExistsException(String message) {
        super(message);
    }
}
