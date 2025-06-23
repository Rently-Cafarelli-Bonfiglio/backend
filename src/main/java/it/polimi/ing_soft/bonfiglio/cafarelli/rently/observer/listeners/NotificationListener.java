package it.polimi.ing_soft.bonfiglio.cafarelli.rently.observer.listeners;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Booking;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChangeRole;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationListener implements EventListener {

    private final NotificationService notificationService;

    @Override
    public void update(String eventType, Object data) {
        if ("BOOKING_CREATED".equals(eventType) && data instanceof Booking booking) {
            // Crea una notifica in-app per il cliente
            notificationService.createNotification(
                    booking.getUser().getUsername(),
                    ("La tua prenotazione è stata confermata! Codice di conferma: " + booking.getBookingConfirmationCode()),
                    "success"
            );
            // Crea una notifica in-app per l'host
            notificationService.createNotification(booking.getProperty().getHost().getUsername(), ("Una nuova prenotazione è stata effettuata per la tua proprietà: " + booking.getProperty().getTitle()), "success");
        } else if ("BOOKING_CANCELED".equals(eventType) && data instanceof  Booking booking ) {

            // Crea una notifica in-app per il cliente
            notificationService.createNotification(
                    booking.getUser().getUsername(),
                    "La tua prenotazione è stata cancellata.",
                    "success"
            );
            // Crea una notifica in-app per l'host
            notificationService.createNotification(booking.getProperty().getHost().getUsername(), ("La prenotazione per la tua proprietà: " + booking.getProperty().getTitle() + " è stata cancellata."), "error");

        } else if ("CHANGEROLE_ACCEPTED".equals(eventType) && data instanceof ChangeRole changeRole) {
            String username = (changeRole.getUser().getUsername());
            String message = "La tua richiesta di cambio ruolo è stata accettata!";
            String type = "success";
            //Crea una notifica in-app per l'utente
            notificationService.createNotification(username, message, "success");
        } else if ("CHANGEROLE_REJECTED".equals(eventType) && data instanceof ChangeRole changeRole) {
            String username = (changeRole.getUser().getUsername());
            String message = "La tua richiesta di cambio ruolo è stata rifiutata.";
            String type = "error";
            //Crea una notifica in-app per l'utente
            notificationService.createNotification(username, message, "error");
        }
    }
}
