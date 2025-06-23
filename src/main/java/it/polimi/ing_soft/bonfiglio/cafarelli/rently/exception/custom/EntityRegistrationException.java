package it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom;

/**
 * Custom exception class for entity registration errors.
 * This exception is thrown when an entity registration fails.
 */
public class EntityRegistrationException extends RuntimeException {
    public <T> EntityRegistrationException(Class<T> entityClass) {
        super(entityClass.getSimpleName() + " registration failed");
    }

    public EntityRegistrationException(String message) {
        super(message);
    }
}
