package it.polimi.ing_soft.bonfiglio.cafarelli.rently.observer.listeners;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Booking;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChangeRole;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class NotificationListenerTest {

    private NotificationService notificationService;
    private NotificationListener notificationListener;

    @BeforeEach
    void setUp() {
        notificationService = mock(NotificationService.class);
        notificationListener = new NotificationListener(notificationService);
    }

    @Test
    void update_BookingCreated_ShouldNotifyUserAndHost() {
        User user = new User();
        user.setUsername("clientUser");

        User host = new User();
        host.setUsername("hostUser");

        Property property = new Property();
        property.setTitle("Appartamento Centro");
        property.setHost(host);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setProperty(property);
        booking.setBookingConfirmationCode("ABC123");

        notificationListener.update("BOOKING_CREATED", booking);

        verify(notificationService).createNotification("clientUser", "La tua prenotazione è stata confermata! Codice di conferma: ABC123", "success");
        verify(notificationService).createNotification("hostUser", "Una nuova prenotazione è stata effettuata per la tua proprietà: Appartamento Centro", "success");
    }

    @Test
    void update_BookingCanceled_ShouldNotifyUserAndHost() {
        User user = new User();
        user.setUsername("clientUser");

        User host = new User();
        host.setUsername("hostUser");

        Property property = new Property();
        property.setTitle("Villa al mare");
        property.setHost(host);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setProperty(property);

        notificationListener.update("BOOKING_CANCELED", booking);

        verify(notificationService).createNotification("clientUser", "La tua prenotazione è stata cancellata.", "success");
        verify(notificationService).createNotification("hostUser", "La prenotazione per la tua proprietà: Villa al mare è stata cancellata.", "error");
    }

    @Test
    void update_ChangeRoleAccepted_ShouldNotifyUser() {
        User user = new User();
        user.setUsername("roleUser");

        ChangeRole changeRole = new ChangeRole();
        changeRole.setUser(user);

        notificationListener.update("CHANGEROLE_ACCEPTED", changeRole);

        verify(notificationService).createNotification("roleUser", "La tua richiesta di cambio ruolo è stata accettata!", "success");
    }

    @Test
    void update_ChangeRoleRejected_ShouldNotifyUser() {
        User user = new User();
        user.setUsername("rejectedUser");

        ChangeRole changeRole = new ChangeRole();
        changeRole.setUser(user);

        notificationListener.update("CHANGEROLE_REJECTED", changeRole);

        verify(notificationService).createNotification("rejectedUser", "La tua richiesta di cambio ruolo è stata rifiutata.", "error");
    }

    @Test
    void update_UnknownEvent_ShouldDoNothing() {
        notificationListener.update("UNKNOWN_EVENT", new Object());

        verifyNoInteractions(notificationService);
    }
}