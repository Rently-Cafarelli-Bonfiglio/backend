package it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition.NotificationBuilder;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Notification;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;

public class NotificationBuilderImplementation implements NotificationBuilder {
    private Long id;
    private String message;
    private String type;
    private boolean read;
    private User user;

    @Override
    public NotificationBuilderImplementation id(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public NotificationBuilderImplementation message(String message) {
        this.message = message;
        return this;
    }

    @Override
    public NotificationBuilderImplementation type(String type) {
        this.type = type;
        return this;
    }

    @Override
    public NotificationBuilderImplementation read(boolean read) {
        this.read = read;
        return this;
    }

    @Override
    public NotificationBuilderImplementation user(User user) {
        this.user = user;
        return this;
    }

    @Override
    public Notification build() { return new Notification( id, message, type, read, null,  user); }
}
