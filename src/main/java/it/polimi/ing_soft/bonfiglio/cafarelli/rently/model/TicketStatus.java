package it.polimi.ing_soft.bonfiglio.cafarelli.rently.model;

public enum TicketStatus {
    OPEN,
    IN_PROGRESS,
    SOLVED,
    CLOSED;

    public static TicketStatus fromString(String status) {
        for (TicketStatus ticketStatus : TicketStatus.values()) {
            if (ticketStatus.name().equalsIgnoreCase(status)) {
                return ticketStatus;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }
}
