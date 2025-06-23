package it.polimi.ing_soft.bonfiglio.cafarelli.rently.config;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.observer.EventManager;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.observer.listeners.NotificationListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class ObserverConfigTest {

    @Mock
    private EventManager eventManager;

    @Mock
    private NotificationListener notificationListener;

    @InjectMocks
    private ObserverConfig observerConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        observerConfig = new ObserverConfig(eventManager, notificationListener);
    }

    @Test
    void configureEventListeners_ShouldSubscribeToEvents() {
        observerConfig.configureEventListeners();

        verify(eventManager).subscribe("BOOKING_CREATED", notificationListener);
        verify(eventManager).subscribe("BOOKING_CANCELED", notificationListener);
        verify(eventManager).subscribe("CHANGEROLE_ACCEPTED", notificationListener);
        verify(eventManager).subscribe("CHANGEROLE_REJECTED", notificationListener);
    }
}
