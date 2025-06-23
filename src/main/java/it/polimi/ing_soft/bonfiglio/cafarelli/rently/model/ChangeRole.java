package it.polimi.ing_soft.bonfiglio.cafarelli.rently.model;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.changeRole.ChangeRoleState;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.changeRole.impl.Accepted;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.changeRole.impl.Pending;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.state.changeRole.impl.Rejected;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * This class represents a change role request made by a user.
 * It contains information about the request such as the user who made the request,
 * the status of the request, the motivation for the request, and the admin who fulfilled it.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class ChangeRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Transient
    private ChangeRoleState state;

    @Enumerated(EnumType.STRING)
    private ChangeRoleStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime fullfilledAt;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User fullfilledBy;

    private String motivation;

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public ChangeRole(User user, String motivation) {
        this.user = user;
        this.state = new Pending();
        this.motivation = motivation;
        this.status = ChangeRoleStatus.PENDING;
    }

    public void setState(ChangeRoleState state) {
        this.state = state;
        this.status = state.getStatus();
    }

    public void accept() {
        state.accept(this);
    }

    public void reject() {
        state.reject(this);
    }

    public void pending() {
        state.pending(this);
    }

    @PostLoad
    private void initState() {
        switch (status) {
            case PENDING -> state = new Pending();
            case ACCEPTED -> state = new Accepted();
            case REJECTED -> state = new Rejected();
        }
    }

    @Override
    public String toString() {
        return "ChangeRole{" +
                "id=" + id +
                ", user=" + user +
                ", state=" + state +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", fullfilledAt=" + fullfilledAt +
                ", fullfilledBy=" + fullfilledBy +
                ", motivation='" + motivation + '\'' +
                '}';
    }

}

