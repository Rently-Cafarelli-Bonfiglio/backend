// Test class for EventManager
package it.polimi.ing_soft.bonfiglio.cafarelli.rently.observer;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.observer.listeners.EventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class EventManagerTest {

    private EventManager eventManager;

    @BeforeEach
    void setUp() {
        eventManager = new EventManager();
    }

    @Test
    void subscribe_ShouldAddListenerToEventType() {
        EventListener listener = mock(EventListener.class);
        String eventType = "BOOKING_CREATED";

        eventManager.subscribe(eventType, listener);
        eventManager.notify(eventType, "data");

        verify(listener, times(1)).update(eventType, "data");
    }

    @Test
    void unsubscribe_ShouldRemoveListenerFromEventType() {
        EventListener listener = mock(EventListener.class);
        String eventType = "BOOKING_CREATED";

        eventManager.subscribe(eventType, listener);
        eventManager.unsubscribe(eventType, listener);
        eventManager.notify(eventType, "data");

        verify(listener, never()).update(anyString(), any());
    }

    @Test
    void notify_ShouldNotThrow_WhenNoListenersForEventType() {
        // Nessun listener registrato per l'evento
        String eventType = "UNKNOWN_EVENT";

        // Non deve lanciare eccezioni
        eventManager.notify(eventType, "data");
    }

    @Test
    void notify_ShouldCallAllListenersForEventType() {
        EventListener listener1 = mock(EventListener.class);
        EventListener listener2 = mock(EventListener.class);
        String eventType = "NOTIFICATION";

        eventManager.subscribe(eventType, listener1);
        eventManager.subscribe(eventType, listener2);

        eventManager.notify(eventType, "payload");

        verify(listener1, times(1)).update(eventType, "payload");
        verify(listener2, times(1)).update(eventType, "payload");
    }
}