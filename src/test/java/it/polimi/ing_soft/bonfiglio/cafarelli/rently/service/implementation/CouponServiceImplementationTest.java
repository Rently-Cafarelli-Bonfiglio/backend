package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.CouponAlreadyUsedException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.CouponExpiredException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityNotFoundException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Coupon;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.CouponRepository;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CouponServiceImplementationTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CouponServiceImplementation couponService;

    private Coupon coupon;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);

        coupon = new Coupon();
        coupon.setCode("WELCOME10");
        coupon.setExpiryDate(LocalDate.now().plusDays(1));  // valido
        coupon.setDiscountPercentage(new BigDecimal("10"));
        coupon.setDiscountAmount(new BigDecimal("5"));
        coupon.setUsers(new ArrayList<>());
    }

    @Test
    void applyCoupon_validPercentageAndFixedDiscount_shouldApplyCorrectly() {
        BigDecimal originalAmount = new BigDecimal("100");
        when(couponRepository.findByCode("WELCOME10")).thenReturn(Optional.of(coupon));

        BigDecimal result = couponService.applyCoupon(1L, "WELCOME10", originalAmount);

        BigDecimal expected = originalAmount.multiply(new BigDecimal("0.90")).subtract(new BigDecimal("5"));

        // Arrotonda entrambi a 2 cifre decimali per evitare errori di precisione
        expected = expected.setScale(2, RoundingMode.HALF_UP);
        result = result.setScale(2, RoundingMode.HALF_UP);

        assertEquals(0, result.compareTo(expected));
    }

    @Test
    void applyCoupon_couponExpired_shouldThrowException() {
        coupon.setExpiryDate(LocalDate.now().minusDays(1));
        when(couponRepository.findByCode("WELCOME10")).thenReturn(Optional.of(coupon));

        assertThrows(CouponExpiredException.class, () ->
                couponService.applyCoupon(1L, "WELCOME10", new BigDecimal("100"))
        );
    }

    @Test
    void applyCoupon_couponAlreadyUsed_shouldThrowException() {
        coupon.getUsers().add(user);  // lo stesso utente ha già usato il coupon
        when(couponRepository.findByCode("WELCOME10")).thenReturn(Optional.of(coupon));

        assertThrows(CouponAlreadyUsedException.class, () ->
                couponService.applyCoupon(1L, "WELCOME10", new BigDecimal("100"))
        );
    }

    @Test
    void applyCoupon_couponNotFound_shouldThrowException() {
        when(couponRepository.findByCode("FAKE")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                couponService.applyCoupon(1L, "FAKE", new BigDecimal("100"))
        );
    }

    @Test
    void assertUsedCoupon_validInput_shouldSaveUserInCoupon() {
        when(couponRepository.findByCode("WELCOME10")).thenReturn(Optional.of(coupon));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(couponRepository.save(any(Coupon.class))).thenAnswer(i -> i.getArgument(0));

        Coupon result = couponService.assertUsedCoupon(1L, "WELCOME10");

        assertTrue(result.getUsers().contains(user));
        verify(couponRepository).save(result);
    }

    @Test
    void assertUsedCoupon_couponNotFound_shouldThrowException() {
        when(couponRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                couponService.assertUsedCoupon(1L, "INVALID")
        );
    }

    @Test
    void assertUsedCoupon_userNotFound_shouldThrowException() {
        when(couponRepository.findByCode("WELCOME10")).thenReturn(Optional.of(coupon));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                couponService.assertUsedCoupon(1L, "WELCOME10")
        );
    }
}