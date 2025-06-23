package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Coupon;

import java.math.BigDecimal;

/**
 * This interface defines the contract for coupon services.
 * It includes methods for applying coupons to total prices.
 */
public interface CouponService {

    /**
     * Applies a coupon to the total price for a user.
     *
     * @param userId the ID of the user applying the coupon
     * @param couponCode the code of the coupon to be applied
     * @param totalPrice the total price before applying the coupon
     * @return the total price after applying the coupon
     */

    BigDecimal applyCoupon(Long userId, String couponCode, BigDecimal totalPrice);

    /**
     * Asserts that a coupon has been used by a user.
     *
     * @param userId the ID of the user who used the coupon
     * @param couponCode the code of the coupon that was used
     * @return the Coupon object if the coupon was successfully asserted as used
     */

    Coupon assertUsedCoupon(Long userId, String couponCode);
}
