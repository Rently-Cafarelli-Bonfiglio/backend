package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.CouponAlreadyUsedException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.CouponExpiredException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.exception.custom.EntityNotFoundException;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.User;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.CouponService;
import it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.definition.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceImplementationTest {

    @Mock private UserService userService;
    @Mock private CouponService couponService;

    @InjectMocks private PaymentServiceImplementation paymentService;

    private User client;
    private User host;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup client user
        client = new User();
        client.setId(1L);
        client.setUsername("client");
        client.setBalance(new BigDecimal("500"));
        
        // Setup host user
        host = new User();
        host.setId(2L);
        host.setUsername("host");
        host.setBalance(new BigDecimal("1000"));
    }

    @Test
    void payForAccomodation_withoutCoupon_success() {
        // Arrange
        BigDecimal amount = new BigDecimal("200");
        when(userService.deductBalance("client", amount)).thenReturn(true);
        
        // Act
        boolean result = paymentService.payForAccomodation("host", "client", amount, null);
        
        // Assert
        assertTrue(result);
        verify(userService).deductBalance("client", amount);
        verify(userService).rechargeBalance("host", amount);
    }
    
    @Test
    void payForAccomodation_withoutCoupon_insufficientFunds_returnsFalse() {
        // Arrange
        BigDecimal amount = new BigDecimal("600");
        when(userService.deductBalance("client", amount)).thenReturn(false);
        
        // Act
        boolean result = paymentService.payForAccomodation("host", "client", amount, null);
        
        // Assert
        assertFalse(result);
        verify(userService).deductBalance("client", amount);
        verify(userService, never()).rechargeBalance(anyString(), any(BigDecimal.class));
    }
    
    @Test
    void payForAccomodation_withCoupon_success() throws EntityNotFoundException, CouponExpiredException, CouponAlreadyUsedException {
        // Arrange
        BigDecimal originalAmount = new BigDecimal("200");
        BigDecimal discountedAmount = new BigDecimal("160");
        String couponCode = "DISCOUNT20";
        
        when(userService.findByUsername("client")).thenReturn(client);
        when(couponService.applyCoupon(1L, couponCode, originalAmount)).thenReturn(discountedAmount);
        when(userService.deductBalance("client", discountedAmount)).thenReturn(true);
        
        // Act
        boolean result = paymentService.payForAccomodation("host", "client", originalAmount, couponCode);
        
        // Assert
        assertTrue(result);
        verify(couponService).applyCoupon(1L, couponCode, originalAmount);
        verify(userService).deductBalance("client", discountedAmount);
        verify(userService).rechargeBalance("host", discountedAmount);
    }
    
    @Test
    void payForAccomodation_withCoupon_insufficientFunds_returnsFalse() throws EntityNotFoundException, CouponExpiredException, CouponAlreadyUsedException {
        // Arrange
        BigDecimal originalAmount = new BigDecimal("600");
        BigDecimal discountedAmount = new BigDecimal("540");
        String couponCode = "DISCOUNT10";
        
        when(userService.findByUsername("client")).thenReturn(client);
        when(couponService.applyCoupon(1L, couponCode, originalAmount)).thenReturn(discountedAmount);
        when(userService.deductBalance("client", discountedAmount)).thenReturn(false);
        
        // Act
        boolean result = paymentService.payForAccomodation("host", "client", originalAmount, couponCode);
        
        // Assert
        assertFalse(result);
        verify(couponService).applyCoupon(1L, couponCode, originalAmount);
        verify(userService).deductBalance("client", discountedAmount);
        verify(userService, never()).rechargeBalance(anyString(), any(BigDecimal.class));
    }
    
    @Test
    void payForAccomodation_couponExpired_throwsException() throws EntityNotFoundException, CouponExpiredException, CouponAlreadyUsedException {
        // Arrange
        BigDecimal amount = new BigDecimal("200");
        String couponCode = "EXPIRED";
        
        when(userService.findByUsername("client")).thenReturn(client);
        when(couponService.applyCoupon(1L, couponCode, amount)).thenThrow(new CouponExpiredException());
        
        // Act & Assert
        assertThrows(CouponExpiredException.class, () -> 
            paymentService.payForAccomodation("host", "client", amount, couponCode)
        );
        verify(userService, never()).deductBalance(anyString(), any(BigDecimal.class));
        verify(userService, never()).rechargeBalance(anyString(), any(BigDecimal.class));
    }
    
    @Test
    void payForAccomodation_couponAlreadyUsed_throwsException() throws EntityNotFoundException, CouponExpiredException, CouponAlreadyUsedException {
        // Arrange
        BigDecimal amount = new BigDecimal("200");
        String couponCode = "USED";
        
        when(userService.findByUsername("client")).thenReturn(client);
        when(couponService.applyCoupon(1L, couponCode, amount)).thenThrow(new CouponAlreadyUsedException());
        
        // Act & Assert
        assertThrows(CouponAlreadyUsedException.class, () -> 
            paymentService.payForAccomodation("host", "client", amount, couponCode)
        );
        verify(userService, never()).deductBalance(anyString(), any(BigDecimal.class));
        verify(userService, never()).rechargeBalance(anyString(), any(BigDecimal.class));
    }
    
    @Test
    void payForAccomodation_userNotFound_throwsException() throws EntityNotFoundException {
        // Arrange
        BigDecimal amount = new BigDecimal("200");
        String couponCode = "DISCOUNT20";
        
        when(userService.findByUsername("nonexistent")).thenThrow(new EntityNotFoundException(User.class));
        
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            paymentService.payForAccomodation("host", "nonexistent", amount, couponCode)
        );
        verify(userService, never()).deductBalance(anyString(), any(BigDecimal.class));
        verify(userService, never()).rechargeBalance(anyString(), any(BigDecimal.class));
    }
}