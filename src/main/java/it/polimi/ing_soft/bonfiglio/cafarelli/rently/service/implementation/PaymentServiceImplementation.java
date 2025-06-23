package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.CouponAlreadyUsedException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.CouponExpiredException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityNotFoundException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.CouponService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.PaymentService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImplementation implements PaymentService {
    private final UserService userService;

    private final CouponService couponService;

    @Transactional
    public boolean payForAccomodation(String hostUsername, String username, BigDecimal total, String couponCode) throws CouponExpiredException, CouponAlreadyUsedException, EntityNotFoundException {

        if(couponCode!=null) {
            BigDecimal discountedPrice;
            Long userId = userService.findByUsername(username).getId();

            discountedPrice = couponService.applyCoupon(userId, couponCode, total);


            if(userService.deductBalance(username, discountedPrice)) {
                userService.rechargeBalance(hostUsername, discountedPrice);
                return true;
            } else {
                return false;
            }
        }

        if(userService.deductBalance(username, total)) {
            userService.rechargeBalance(hostUsername, total);
            return true;
        } else {
            return false;
        }
    }
}
