package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.CouponAlreadyUsedException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.CouponExpiredException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityNotFoundException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Coupon;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.CouponRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.UserRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.CouponService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * This class implements the CouponService interface, providing methods for applying coupons to total prices.
 * It uses a CouponRepository to interact with the database.
 */
@Service
@AllArgsConstructor
public class CouponServiceImplementation implements CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BigDecimal applyCoupon(Long userId, String couponCode, BigDecimal originalAmount) {
        Coupon coupon = couponRepository.findByCode(couponCode).orElseThrow(() ->
                 new EntityNotFoundException(Coupon.class)
        );

        if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
            throw new CouponExpiredException();
        }

        if (coupon.getUsers().stream().anyMatch(user -> user.getId().equals(userId))) {
            throw new CouponAlreadyUsedException();
        }

        BigDecimal discountedAmount = originalAmount;



        // Apply percentage discount, if present
        if (coupon.getDiscountPercentage() != null) {
            BigDecimal discountFactor = BigDecimal.ONE.subtract(
                    coupon.getDiscountPercentage().divide(new BigDecimal("100")));
            discountedAmount = originalAmount.multiply(discountFactor);
        }

        // Apply fixed discount amount, if present
        if (coupon.getDiscountAmount() != null) {
            discountedAmount = discountedAmount.subtract(coupon.getDiscountAmount());
            // Ensure the price doesn't become negative
            if (discountedAmount.compareTo(BigDecimal.ZERO) < 0) {
                discountedAmount = BigDecimal.ZERO;
            }
        }

        return discountedAmount;
    }

    @Override
    public Coupon assertUsedCoupon(Long userId, String couponCode) {
        Coupon coupon = couponRepository.findByCode(couponCode).orElseThrow(
                () -> new EntityNotFoundException(Coupon.class)
        );
        coupon.getUsers().add(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class)));
        couponRepository.save(coupon);
        return coupon;
    }
}