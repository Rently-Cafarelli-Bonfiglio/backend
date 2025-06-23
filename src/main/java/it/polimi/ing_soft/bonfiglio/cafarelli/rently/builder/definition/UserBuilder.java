package it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface for building {@link User} objects.
 */
public interface UserBuilder {
    UserBuilder id(Long id);

    UserBuilder firstname(String firstname);

    UserBuilder lastname(String lastname);

    UserBuilder username(String username);

    UserBuilder email(String email);

    UserBuilder password(String password);

    UserBuilder role(Role role);

    UserBuilder imageUrl(String imageUrl);

    UserBuilder sentMessages(List<ChatMessage> sentMessages);

    UserBuilder receivedMessages(List<ChatMessage> receivedMessages);

    UserBuilder properties(List<Property> properties);

    UserBuilder bookings(List<Booking> bookings);

    UserBuilder favorite(List<Property> favorite);

    UserBuilder isActive(boolean isActive);

    UserBuilder balance(BigDecimal balance);

    UserBuilder coupons(List<Coupon> coupons);

    UserBuilder reviews(List<Review> reviews);

    User build();
}