package it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Notification;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;

public interface NotificationBuilder {
    NotificationBuilder id(Long id);
    NotificationBuilder message(String message);
    NotificationBuilder type(String type);
    NotificationBuilder read(boolean read);
    NotificationBuilder user(User user);

    Notification build();
}
