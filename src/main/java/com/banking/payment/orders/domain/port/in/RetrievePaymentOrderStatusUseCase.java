package com.banking.payment.orders.domain.port.in;

import com.banking.payment.orders.domain.model.PaymentOrderProcedure;
import java.util.UUID;
import reactor.core.publisher.Mono;

/**
 * RetrievePaymentOrderStatusUseCase - Puerto de entrada para recuperar estado de orden.
 */
public interface RetrievePaymentOrderStatusUseCase {

  /**
   * Recupera el estado de una orden de pago por su ID.
   *
   * @param paymentOrderId ID de la orden de pago
   * @return Mono con la orden de pago (solo para obtener el estado)
   */
  Mono<PaymentOrderProcedure> retrieveStatus(UUID paymentOrderId);
}
