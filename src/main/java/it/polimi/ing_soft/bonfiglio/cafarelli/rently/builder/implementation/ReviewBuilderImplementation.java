package it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition.ReviewBuilder;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Review;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;

import java.time.LocalDateTime;

/**
 * Implementation of the {@link ReviewBuilder} interface for building {@link Review} objects.
 */
// This class uses the Builder design pattern to create instances of Review.
public class ReviewBuilderImplementation implements ReviewBuilder {

    private long id;
    private String title;
    private String description;
    private User reviewer;
    private Property property;
    private User reviewedUser;
    private LocalDateTime createdAt;
    private int rating;
    private String hostResponse;
    private LocalDateTime hostResponseCreatedAt;

    @Override
    public ReviewBuilder id(long id) {
        this.id = id;
        return this;
    }

    @Override
    public ReviewBuilder title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public ReviewBuilder description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public ReviewBuilder reviewer(User reviewer) {
        this.reviewer = reviewer;
        return this;
    }

    @Override
    public ReviewBuilder property(Property property) {
        this.property = property;
        return this;
    }

    @Override
    public ReviewBuilder reviewedUser(User reviewedUser){
        this.reviewedUser= reviewedUser;
        return this;
    }
    @Override
    public ReviewBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @Override
    public ReviewBuilder rating(int rating) {
        this.rating = rating;
        return this;
    }

    @Override
    public ReviewBuilder hostResponse(String hostResponse) {
        this.hostResponse = hostResponse;
        return this;
    }

    @Override
    public ReviewBuilder hostResponseCreatedAt(LocalDateTime hostResponseCreatedAt) {
        this.hostResponseCreatedAt = hostResponseCreatedAt;
        return this;
    }

    @Override
    public Review build() {
        return new Review(id, title, description, reviewer, property, reviewedUser, createdAt, rating, hostResponse, hostResponseCreatedAt);
    }
}