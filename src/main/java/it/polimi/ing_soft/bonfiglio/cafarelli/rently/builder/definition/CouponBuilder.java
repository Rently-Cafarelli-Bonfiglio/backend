package it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Coupon;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface for building {@link Coupon} objects.
 */
public interface CouponBuilder {
    CouponBuilder id(Long id);

    CouponBuilder code(String code);

    CouponBuilder discountAmount(BigDecimal discountAmount);

    CouponBuilder discountPercentage(BigDecimal discountPercentage);

    CouponBuilder expiryDate(LocalDate expiryDate);

    CouponBuilder users(List<User> users);

    Coupon build();
}
