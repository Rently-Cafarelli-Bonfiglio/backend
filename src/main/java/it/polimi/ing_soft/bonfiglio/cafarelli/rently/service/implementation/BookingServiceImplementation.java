package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.implementation.BookingBuilderImplementation;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request.BookingCreateRequest;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.BookingDashboardResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.CustomResponse;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response.UserSummary;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityNotFoundException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.PaymentRejectedException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.UnavailablePropertyException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.UserUnauthorizedException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Booking;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Role;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.observer.EventManager;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.BookingRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.UserRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.BookingService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.CouponService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.PaymentService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.util.BookingUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class implements the BookingService interface and provides methods for managing bookings.
 * It uses the BookingRepository, UserRepository, and PropertyRepository to interact with the database.
 */
@Service
@AllArgsConstructor
public class BookingServiceImplementation implements BookingService {

    private final BookingRepository bookingRepository;
    private final CouponService couponService;
    private final PaymentService paymentService;
    private final EventManager eventManager;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CustomResponse saveBooking(BookingCreateRequest bookingRequest) {
        User customer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
                throw new IllegalArgumentException("Check-in must be before check-out");
            }

            Property property = bookingRequest.getProperty();

            if (!propertyIsAvailable(bookingRequest)) {
                throw new UnavailablePropertyException("Property not available for the selected dates");
            }

            if(property.getMaxGuests() < (bookingRequest.getNumOfAdults() + bookingRequest.getNumOfChildren())) {
                throw new UnavailablePropertyException("The property cannot accommodate that number of people");
            }

            Booking booking = new BookingBuilderImplementation()
                    .property(property)
                    .user(customer)
                    .checkInDate(bookingRequest.getCheckInDate())
                    .checkOutDate(bookingRequest.getCheckOutDate())
                    .numOfAdults(bookingRequest.getNumOfAdults())
                    .numOfChildren(bookingRequest.getNumOfChildren())
                    .total((property.getPricePerNight().multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate())))))
                    .bookingConfirmationCode(BookingUtils.generateRandomConfirmationCode(10))
                    .build();



            if (paymentService.payForAccomodation(property.getHost().getUsername(), customer.getUsername(), booking.getTotal(), bookingRequest.getCouponCode())) {
                bookingRepository.save(booking);

                if (bookingRequest.getCouponCode() != null) {
                    couponService.assertUsedCoupon(customer.getId(), bookingRequest.getCouponCode());
                }

                eventManager.notify("BOOKING_CREATED", booking);

                return new CustomResponse("Booking confirmed");
            } else {
                throw new PaymentRejectedException("Unable to complete the payment");
            }
    }

    @Override
    public Booking findBookingByConfirmationCode(String confirmationCode) {

            return bookingRepository.findByBookingConfirmationCode(confirmationCode)
                    .orElseThrow(() -> new EntityNotFoundException(Booking.class));
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public List<Booking> getAllBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public CustomResponse cancelBooking(Long bookingId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(Booking.class));

        // Verifica autorizzazione (proprietario della prenotazione, admin o moderator)
        if (!booking.getUser().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().equals(Role.ADMIN) &&
                !currentUser.getRole().equals(Role.MODERATOR)) {
            throw new UserUnauthorizedException("Utente non autorizzato");
        }

        // Recupera utenti prima dell'eliminazione
        User customer = booking.getUser();
        User host = booking.getProperty().getHost();
        BigDecimal refundAmount = booking.getTotal();

        // Aggiorna i saldi
        customer.setBalance(customer.getBalance().add(refundAmount));
        host.setBalance(host.getBalance().subtract(refundAmount));

        // Salva gli utenti con i nuovi saldi
        userRepository.save(customer);
        userRepository.save(host);

        // Elimina la prenotazione
        bookingRepository.delete(booking);

        // Notifica l'evento di cancellazione
        eventManager.notify("BOOKING_CANCELED", booking);

        return new CustomResponse("Prenotazione cancellata con successo");
    }

    @Override
    public List<BookingDashboardResponse> getAllBookingsByHostId(Long hostId) {
        List<Booking> bookings = bookingRepository.findByProperty_Host_Id(hostId);
        return bookings.stream()
                .map(booking -> new BookingDashboardResponse(
                        booking.getProperty().getTitle(),
                        new UserSummary(
                                booking.getUser().getId(),
                                booking.getUser().getFirstname(),
                                booking.getUser().getLastname(),
                                booking.getUser().getEmail()
                        ),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getTotal()
                ))
                .collect(Collectors.toList());
    }


    private boolean propertyIsAvailable(BookingCreateRequest bookingRequest) {
        return !bookingRepository.existsOverlappingBooking(
            bookingRequest.getProperty().getId(),
            bookingRequest.getCheckInDate(),
            bookingRequest.getCheckOutDate()
        );
    }
}