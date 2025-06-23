package it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom;

public class CouponExpiredException extends RuntimeException {
    public CouponExpiredException() {
        super("Coupon is expired");
    }
}
