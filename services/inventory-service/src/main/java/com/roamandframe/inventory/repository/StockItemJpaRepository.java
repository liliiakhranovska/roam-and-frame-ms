package com.roamandframe.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface StockItemJpaRepository extends JpaRepository<StockItemJpaEntity, String> {

    /** Atomically takes the quantity out of stock; returns 0 when there is not enough. */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update StockItemJpaEntity s set s.quantity = s.quantity - :quantity "
            + "where s.sku = :sku and s.quantity >= :quantity")
    int reserve(@Param("sku") String sku, @Param("quantity") int quantity);

    /** Puts the quantity back into stock. */
    @Modifying(flushAutomatically = true)
    @Query("update StockItemJpaEntity s set s.quantity = s.quantity + :quantity where s.sku = :sku")
    int release(@Param("sku") String sku, @Param("quantity") int quantity);
}
