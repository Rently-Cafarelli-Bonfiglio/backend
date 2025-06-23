package it.polimi.ing_soft.bonfiglio.cafarelli.rently.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class for defining API paths.
 * <p>
 * This class contains constants representing the base path and various API endpoints.
 * It is designed to be used throughout the application to ensure consistent URL structure.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiPathUtil {
    public static final String BASE_PATH = "/api";
    public static final String REST_PATH = BASE_PATH + "/v1";
}
