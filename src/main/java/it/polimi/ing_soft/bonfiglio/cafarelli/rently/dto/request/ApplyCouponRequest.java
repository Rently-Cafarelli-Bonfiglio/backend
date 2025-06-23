package it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data

/**
 * Request DTO for applying a coupon to a user's account.
 * Contains the user ID, coupon code, and total amount.
 */

public class ApplyCouponRequest {
    @NotBlank(message = "User cannot be blank")
    Long userId;
    @NotBlank(message = "Coupon code cannot be blank")
    String couponCode;
    @NotBlank(message = "Amount cannot be blank") @Min(value = 0, message = "Amount cannot be less than zero")
    BigDecimal totalAmount;
}
