package com.banking.payment.orders.adapter.out.persistence;

import com.banking.payment.orders.adapter.out.persistence.entity.PaymentOrderEntity;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * PaymentOrderR2dbcRepository - Repositorio R2DBC para órdenes de pago.
 */
@Repository
public interface PaymentOrderR2dbcRepository
    extends ReactiveCrudRepository<PaymentOrderEntity, UUID> {
}
