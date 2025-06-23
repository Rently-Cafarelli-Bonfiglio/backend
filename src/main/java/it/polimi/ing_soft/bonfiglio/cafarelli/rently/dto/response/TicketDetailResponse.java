package it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Ticket;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.TicketReply;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDetailResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime creationDate;
    private LocalDateTime closingDate;
    private String status;
    private User user;
    private List<TicketReply> replies;

    public static TicketDetailResponse fromEntity(Ticket ticket, List<TicketReply> replies) {
        return new TicketDetailResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getCreationDate(),
                ticket.getClosingDate(),
                ticket.getStatus().name(),
                ticket.getUser(),
                replies
        );
    }
}
