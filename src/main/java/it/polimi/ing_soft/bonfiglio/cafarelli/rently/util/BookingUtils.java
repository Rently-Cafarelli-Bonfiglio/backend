package it.polimi.ing_soft.bonfiglio.cafarelli.rently.util;

import java.security.SecureRandom;

/**
 * Utility class for generating random confirmation codes.
 * <p>
 * This class provides a method to generate a random alphanumeric confirmation code of a specified length.
 */
public class BookingUtils {

    private static final String ALPHANUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateRandomConfirmationCode(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(ALPHANUMERIC_STRING.length());
            char randomChar = ALPHANUMERIC_STRING.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }
}
