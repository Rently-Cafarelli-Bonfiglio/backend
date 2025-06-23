package it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Booking;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Review;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import java.util.List;

import java.math.BigDecimal;

/**
 * Interface for building {@link Property} objects.
 */
public interface PropertyBuilder {

    PropertyBuilder propertyId(Long propertyId);

    PropertyBuilder host(User host);

    PropertyBuilder title(String title);

    PropertyBuilder description(String description);

    PropertyBuilder address(String address);

    PropertyBuilder city(String city);

    PropertyBuilder state(String state);

    PropertyBuilder country(String country);

    PropertyBuilder pricePerNight(BigDecimal pricePerNight);

    PropertyBuilder maxGuests(Integer maxGuests);

    PropertyBuilder bedrooms(Integer bedrooms);

    PropertyBuilder bathrooms(Integer bathrooms);

    PropertyBuilder isAvailable(boolean isAvailable);

    PropertyBuilder favoritedBy(List<User> favoritedBy);

    PropertyBuilder bookings(List<Booking> bookings);

    PropertyBuilder propertyImages(List<String> propertyImages);

    PropertyBuilder reviews(List<Review> reviews);

    Property build();
}