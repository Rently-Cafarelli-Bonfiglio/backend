package it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.ChatMessage;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing ChatMessage entities.
 * It extends JpaRepository to provide CRUD operations and custom query methods.
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    /**
     * Finds all chat messages exchanged between two users.
     *
     * @param user1 the first user
     * @param user2 the second user
     * @return a list of ChatMessage objects representing the conversation between the two users
     */
    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.sender = :user1 AND m.receiver = :user2) OR " +
            "(m.sender = :user2 AND m.receiver = :user1) " +
            "ORDER BY m.sendAt")
    List<ChatMessage> findConversationBetweenUsers(User user1, User user2);

    /**
     * Finds all distinct chat messages by receiver.
     *
     * @param user the user who received the messages
     * @return a list of distinct ChatMessage objects received by the specified user
     */
    List<ChatMessage> findDistinctByReceiver(User user);
}
