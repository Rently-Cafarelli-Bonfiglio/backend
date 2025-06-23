package it.polimi.ing_soft.bonfiglio.cafarelli.rently.config;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.observer.EventManager;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.observer.listeners.NotificationListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
public class ObserverConfig {

    private final EventManager eventManager;
    private final NotificationListener notificationListener;

    @PostConstruct
    public void configureEventListeners() {
        // Registra i listener per l'evento BOOKING_CREATED
        eventManager.subscribe("BOOKING_CREATED", notificationListener);
        eventManager.subscribe("BOOKING_CANCELED", notificationListener);
        eventManager.subscribe("CHANGEROLE_ACCEPTED", notificationListener);
        eventManager.subscribe("CHANGEROLE_REJECTED", notificationListener);
    }
}
