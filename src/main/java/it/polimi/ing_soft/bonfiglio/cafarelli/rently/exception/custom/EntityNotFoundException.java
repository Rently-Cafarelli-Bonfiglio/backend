package it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom;

/**
 * Custom exception class for entity not found errors.
 * This exception is thrown when an entity is not found in the database.
 */
public class EntityNotFoundException extends RuntimeException{
    public <T> EntityNotFoundException(Class<T> entityClass) {
        super(entityClass.getSimpleName() + " not found");
    }
}
