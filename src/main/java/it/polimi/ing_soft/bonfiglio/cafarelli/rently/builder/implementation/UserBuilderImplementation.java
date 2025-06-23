package it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition.UserBuilder;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Coupon;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Review;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Role;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link UserBuilder} interface for building {@link User} objects.
 */
// This class uses the Builder design pattern to create instances of User.
@NoArgsConstructor
public class UserBuilderImplementation implements UserBuilder {
    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String password;
    private Role role;
    private String imageUrl;
    private List sentMessages;
    private List receivedMessages;
    private List properties;
    private List bookings;
    private List favorite;
    private boolean isActive;
    private BigDecimal balance;
    private List coupons;
    private List reviews;

    @Override
    public UserBuilder id(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public UserBuilder firstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    @Override
    public UserBuilder lastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    @Override
    public UserBuilder username(String username) {
        this.username = username;
        return this;
    }

    @Override
    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    @Override
    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }

    @Override
    public UserBuilder role(Role role) {
        this.role = role;
        return this;
    }

    @Override
    public UserBuilder imageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    @Override
    public UserBuilder sentMessages(List sentMessages) {
        this.sentMessages = sentMessages;
        return this;
    }

    @Override
    public UserBuilder receivedMessages(List receivedMessages) {
        this.receivedMessages = receivedMessages;
        return this;
    }

    @Override
    public UserBuilder properties(List properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public UserBuilder bookings(List bookings) {
        this.bookings = bookings;
        return this;
    }

    @Override
    public UserBuilder favorite(List favorite) {
        this.favorite = favorite;
        return this;
    }

    @Override
    public UserBuilder isActive(boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    @Override
    public UserBuilder balance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    @Override
    public UserBuilder coupons(List<Coupon> coupons) {
        this.coupons = coupons;
        return this;
    }

    @Override
    public UserBuilder reviews(List<Review> reviews) {
        this.reviews = reviews;
        return this;
    }

    @Override
    public User build() {
        return new User(id, firstname, lastname, username, email, password, role, null, null, imageUrl, sentMessages, receivedMessages, properties, bookings, favorite, isActive, balance, coupons, reviews, new ArrayList<>());
    }
}