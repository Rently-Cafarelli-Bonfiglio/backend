package it.polimi.ing_soft.bonfiglio.cafarelli.rently.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing the different roles a user can have in the system.
 * The roles are:
 * - CLIENT: A regular user of the system.
 * - HOST: A user who can host properties.
 * - MODERATOR: A user with moderation privileges.
 * - ADMIN: An administrator with full access to the system.
 */
@Getter
@RequiredArgsConstructor
public enum Role {
    CLIENT,
    HOST,
    MODERATOR,
    ADMIN;
}
