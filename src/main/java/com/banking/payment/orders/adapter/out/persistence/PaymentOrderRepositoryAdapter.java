package com.banking.payment.orders.adapter.out.persistence;

import com.banking.payment.orders.adapter.out.persistence.mapper.PaymentOrderPersistenceMapper;
import com.banking.payment.orders.domain.model.PaymentOrderProcedure;
import com.banking.payment.orders.domain.port.out.PaymentOrderPort;
import java.util.UUID;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * PaymentOrderRepositoryAdapter - Adaptador de persistencia para órdenes de pago.
 */
@Component
public class PaymentOrderRepositoryAdapter implements PaymentOrderPort {

  private final PaymentOrderR2dbcRepository repository;
  private final PaymentOrderPersistenceMapper mapper;

  public PaymentOrderRepositoryAdapter(
      PaymentOrderR2dbcRepository repository,
      PaymentOrderPersistenceMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public Mono<PaymentOrderProcedure> save(PaymentOrderProcedure paymentOrder) {
    return repository.findById(paymentOrder.getPaymentOrderProcedureInstanceReference())
        .flatMap(existingEntity -> {
          // UPDATE: La entidad existe, actualizar
          var entity = mapper.toEntity(paymentOrder, false);
          return repository.save(entity);
        })
        .switchIfEmpty(Mono.defer(() -> {
          // INSERT: La entidad no existe, crear nueva
          var entity = mapper.toEntity(paymentOrder, true);
          return repository.save(entity);
        }))
        .map(mapper::toDomain);
  }

  @Override
  public Mono<PaymentOrderProcedure> findById(UUID paymentOrderId) {
    return repository.findById(paymentOrderId)
        .map(mapper::toDomain);
  }
}
