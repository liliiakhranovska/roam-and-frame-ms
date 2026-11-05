package com.roamandframe.checkout.repository;

import com.roamandframe.checkout.model.CheckoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

interface CheckoutJpaRepository extends JpaRepository<CheckoutJpaEntity, UUID> {

    List<CheckoutJpaEntity> findByStatusInAndUpdatedAtBefore(Collection<CheckoutStatus> statuses, Instant before);

    @Modifying
    @Query("update CheckoutJpaEntity c set c.lastStep = :step, c.updatedAt = :now where c.id = :id")
    int updateStep(@Param("id") UUID id, @Param("step") int step, @Param("now") Instant now);

    @Modifying
    @Query("update CheckoutJpaEntity c set c.status = :status, c.failureReason = :reason, c.updatedAt = :now "
            + "where c.id = :id")
    int updateStatus(@Param("id") UUID id, @Param("status") CheckoutStatus status,
                     @Param("reason") String reason, @Param("now") Instant now);

    @Modifying
    @Query("update CheckoutJpaEntity c set c.updatedAt = :now where c.id = :id and c.updatedAt = :expected")
    int touchIfUnchanged(@Param("id") UUID id, @Param("expected") Instant expected, @Param("now") Instant now);
}
