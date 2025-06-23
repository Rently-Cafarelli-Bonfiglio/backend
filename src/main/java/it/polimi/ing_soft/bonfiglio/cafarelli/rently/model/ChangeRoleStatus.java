package it.polimi.ing_soft.bonfiglio.cafarelli.rently.model;

/**
 * Enum representing the status of a change role request.
 * The status can be one of the following:
 * - PENDING: The request is pending approval.
 * - ACCEPTED: The request has been accepted.
 * - REJECTED: The request has been rejected.
 */
public enum ChangeRoleStatus {
    PENDING,
    ACCEPTED,
    REJECTED;

    public static ChangeRoleStatus fromString(String status) {
        for (ChangeRoleStatus changeRoleStatus : ChangeRoleStatus.values()) {
            if (changeRoleStatus.name().equalsIgnoreCase(status)) {
                return changeRoleStatus;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }
}
