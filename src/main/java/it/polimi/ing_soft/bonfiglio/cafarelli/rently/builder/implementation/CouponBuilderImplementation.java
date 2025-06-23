package it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.builder.definition.CouponBuilder;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Coupon;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of the {@link CouponBuilder} interface for building {@link Coupon} objects.
 */
// This class uses the Builder design pattern to create instances of Coupon.

@NoArgsConstructor
public class CouponBuilderImplementation implements CouponBuilder {
    private Long id;
    private String code;
    private BigDecimal discountAmount;
    private BigDecimal discountPercentage;
    private LocalDate expiryDate;
    private List<User> users;

    @Override
    public CouponBuilderImplementation id(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public CouponBuilderImplementation code(String code) {
        this.code = code;
        return this;
    }

    @Override
    public CouponBuilderImplementation discountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
        return this;
    }

    @Override
    public CouponBuilderImplementation discountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
        return this;
    }

    @Override
    public CouponBuilderImplementation expiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }

    @Override
    public CouponBuilderImplementation users(List<User> users) {
        this.users = users;
        return this;
    }

    @Override
    public Coupon build() {
        return new Coupon(id, code, discountAmount, discountPercentage, expiryDate, users);
    }
}
