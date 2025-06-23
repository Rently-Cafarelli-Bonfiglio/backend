package it.polimi.ing_soft.bonfiglio.cafarelli.rently.repository;

import it.polimi.ing_soft.bonfiglio.cafarelli.rently.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing Coupon entities.
 * It extends JpaRepository to provide CRUD operations and custom query methods.
 */
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    /**
     * Finds a Coupon by its code.
     *
     * @param code the code of the coupon
     * @return an Optional containing the Coupon if found, or empty if not found
     */

    Optional<Coupon> findByCode(String code);
}