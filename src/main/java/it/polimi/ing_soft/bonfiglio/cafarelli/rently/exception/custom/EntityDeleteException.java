package it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom;

/**
 * Custom exception class for entity deletion errors.
 * This exception is thrown when an entity cannot be deleted.
 */
public class EntityDeleteException extends RuntimeException {
    public <T> EntityDeleteException(Class<T> entityClass) {
        super(entityClass.getSimpleName() + " delete failed");
    }

    public EntityDeleteException(String message) {
        super(message);
    }
}
