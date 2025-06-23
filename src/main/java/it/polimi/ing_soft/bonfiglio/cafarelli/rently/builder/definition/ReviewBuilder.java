package it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Property;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Review;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;

import java.time.LocalDateTime;

/**
 * Interface for building {@link Review} objects.
 */
public interface ReviewBuilder {

    ReviewBuilder id(long id);

    ReviewBuilder title(String title);

    ReviewBuilder description(String description);

    ReviewBuilder reviewer(User reviewer);

    ReviewBuilder property(Property property);

    ReviewBuilder reviewedUser(User reviewedUser);

    ReviewBuilder createdAt(LocalDateTime createdAt);

    ReviewBuilder rating(int rating);

    ReviewBuilder hostResponse(String hostResponse);

    ReviewBuilder hostResponseCreatedAt(LocalDateTime hostResponseCreatedAt);

    Review build();
}
