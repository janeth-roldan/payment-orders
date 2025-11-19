package com.banking.payment.orders.domain.port.in;

import com.banking.payment.orders.domain.model.PaymentOrderProcedure;
import java.util.UUID;
import reactor.core.publisher.Mono;

/**
 * RetrievePaymentOrderUseCase - Puerto de entrada para recuperar una orden de pago.
 */
public interface RetrievePaymentOrderUseCase {

  /**
   * Recupera una orden de pago por su ID.
   *
   * @param paymentOrderId ID de la orden de pago
   * @return Mono con la orden de pago
   */
  Mono<PaymentOrderProcedure> retrieve(UUID paymentOrderId);
}
