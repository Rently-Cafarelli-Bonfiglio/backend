package it.polimi.ing_soft.bonfiglio.cafarelli.rently.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BookingDashboardResponse {
    private String title;
    private UserSummary user;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal total;
}
