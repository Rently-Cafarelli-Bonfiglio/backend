package it.polimi.ing_soft.bonfiglio.cafarelli.rently.model;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.ticket.TicketState;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.ticket.impl.Closed;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.ticket.impl.InProgress;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.ticket.impl.Open;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.ticket.impl.Solved;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private LocalDateTime creationDate;

    private LocalDateTime closingDate;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Transient
    private TicketState state;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @PrePersist
    private void onCreate() {
        creationDate = LocalDateTime.now();
    }

    public Ticket(String title, String description, User user) {
        this.title = title;
        this.description = description;
        this.user = user;
        this.state = new Open();
        this.status = TicketStatus.OPEN;
        this.creationDate = LocalDateTime.now();
    }


    public void setState(TicketState state) {
        this.state = state;
        this.status = state.getStatus();
    }

    public void open(){ state.open(this); }
    public void inProgress() { state.inProgress(this); }
    public void solved() { state.solved(this); }
    public void closed() { state.closed(this); }

    @PostLoad
    private void initState() {
        switch(status){
            case OPEN -> state = new Open();
            case IN_PROGRESS -> state = new InProgress();
            case SOLVED -> state = new Solved();
            case CLOSED -> state = new Closed();
        }
    }



}
