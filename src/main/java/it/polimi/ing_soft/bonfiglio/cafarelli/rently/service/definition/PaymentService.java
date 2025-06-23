package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition;

import java.math.BigDecimal;

public interface PaymentService {

    /**
     * Processes a payment for accommodation.
     *
     * @param hostUsername the username of the host
     * @param username the username of the user making the payment
     * @param total the total amount to be paid
     * @param couponCode the coupon code to be applied, if any
     * @return true if the payment was successful, false otherwise
     */

    boolean payForAccomodation(String hostUsername, String username, BigDecimal total, String couponCode);
}
