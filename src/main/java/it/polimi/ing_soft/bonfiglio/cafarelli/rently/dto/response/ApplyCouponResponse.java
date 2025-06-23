package it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ApplyCouponResponse {
    String couponCode;
    BigDecimal discountedAmount;
}
