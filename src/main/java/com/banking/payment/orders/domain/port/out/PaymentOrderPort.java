package com.banking.payment.orders.domain.port.out;

import com.banking.payment.orders.domain.model.PaymentOrderProcedure;
import java.util.UUID;
import reactor.core.publisher.Mono;

/**
 * PaymentOrderPort - Puerto de salida para persistencia de órdenes de pago.
 */
public interface PaymentOrderPort {

  /**
   * Guarda una orden de pago.
   *
   * @param paymentOrder orden de pago a guardar
   * @return Mono con la orden de pago guardada
   */
  Mono<PaymentOrderProcedure> save(PaymentOrderProcedure paymentOrder);

  /**
   * Busca una orden de pago por su ID.
   *
   * @param paymentOrderId ID de la orden de pago
   * @return Mono con la orden de pago encontrada o vacío
   */
  Mono<PaymentOrderProcedure> findById(UUID paymentOrderId);
}
