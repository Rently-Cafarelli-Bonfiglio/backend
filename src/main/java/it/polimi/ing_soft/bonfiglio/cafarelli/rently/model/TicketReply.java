package it.polimi.ing_soft.bonfiglio.cafarelli.rently.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name="ticket_id", nullable=false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    private boolean fromModerator;

    @PrePersist
    private void onCreate() {
        creationDate = LocalDateTime.now();
    }
}
