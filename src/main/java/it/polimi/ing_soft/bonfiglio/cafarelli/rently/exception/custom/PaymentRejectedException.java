package it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom;

public class PaymentRejectedException extends RuntimeException {
    public PaymentRejectedException(String message) {
        super(message);
    }
}
