package it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom;

/**
 * Custom exception class for entity deletion errors.
 * This exception is thrown when an entity cannot be deleted.
 */
public class EntityModifyException extends RuntimeException {
    public <T> EntityModifyException(Class<T> entityClass) {
        super(entityClass.getSimpleName() + " registration failed");
    }

    public EntityModifyException(String message) {
        super(message);
    }
}
