package it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom;

public class CouponAlreadyUsedException extends RuntimeException {

    public CouponAlreadyUsedException() {
        super("Coupon already used");
    }
}
