package it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition.PropertyBuilder;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Booking;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Review;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation of the {@link PropertyBuilder} interface for building {@link Property} objects.
 */
// This class uses the Builder design pattern to create instances of Property.
@Component
@NoArgsConstructor
public class PropertyBuilderImplementation implements PropertyBuilder {
    private Long propertyId;
    private User host;
    private String title;
    private String description;
    private String address;
    private String city;
    private String state;
    private String country;
    private BigDecimal pricePerNight;
    private Integer maxGuests;
    private Integer bedrooms;
    private Integer bathrooms;
    private boolean isAvailable = true;
    private List<User> favoritedBy;
    private List<Booking> bookings;
    private List<String> propertyImages;
    private List<Review> reviews;

    @Override
    public PropertyBuilder propertyId(Long propertyId) {
        this.propertyId = propertyId;
        return this;
    }

    @Override
    public PropertyBuilder host(User host) {
        this.host = host;
        return this;
    }

    @Override
    public PropertyBuilder title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public PropertyBuilder description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public PropertyBuilder address(String address) {
        this.address = address;
        return this;
    }

    @Override
    public PropertyBuilder city(String city) {
        this.city = city;
        return this;
    }

    @Override
    public PropertyBuilder state(String state) {
        this.state = state;
        return this;
    }

    @Override
    public PropertyBuilder country(String country) {
        this.country = country;
        return this;
    }

    @Override
    public PropertyBuilder pricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
        return this;
    }

    @Override
    public PropertyBuilder maxGuests(Integer maxGuests) {
        this.maxGuests = maxGuests;
        return this;
    }

    @Override
    public PropertyBuilder bedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
        return this;
    }

    @Override
    public PropertyBuilder bathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
        return this;
    }

    @Override
    public PropertyBuilder isAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
        return this;
    }

    @Override
    public PropertyBuilder favoritedBy(List<User> favoritedBy) {
        this.favoritedBy = favoritedBy;
        return this;
    }

    @Override
    public PropertyBuilder bookings(List<Booking> bookings) {
        this.bookings = bookings;
        return this;
    }

    @Override
    public PropertyBuilder propertyImages(List<String> propertyImages) {
        this.propertyImages = propertyImages;
        return this;
    }

    @Override
    public PropertyBuilder reviews(List<Review> reviews) {
        this.reviews = reviews;
        return this;
    }

    @Override
    public Property build() {
        return new Property(propertyId, host, title, description, address, city, state, country, pricePerNight, maxGuests, bedrooms, bathrooms, isAvailable, null, null, favoritedBy, bookings, propertyImages, reviews);
    }
}