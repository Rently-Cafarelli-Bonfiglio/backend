package it.polimi.ing_soft.bonfiglio.cafarelli.rently.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a coupon that can be used for discounts on bookings.
 * It contains information about the coupon code, discount amount, percentage, and expiry date.
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Coupon implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column
    private BigDecimal discountAmount;

    @Column
    private BigDecimal discountPercentage;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="used_coupon",
            joinColumns = @JoinColumn(name="code"),
            inverseJoinColumns = @JoinColumn(name="user_id"))

    private List<User> users = new ArrayList<>();
}
