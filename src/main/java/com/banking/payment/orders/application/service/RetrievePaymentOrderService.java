package com.banking.payment.orders.application.service;

import com.banking.payment.orders.domain.exception.PaymentOrderNotFoundException;
import com.banking.payment.orders.domain.model.PaymentOrderProcedure;
import com.banking.payment.orders.domain.port.in.RetrievePaymentOrderUseCase;
import com.banking.payment.orders.domain.port.out.PaymentOrderPort;
import java.util.UUID;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * RetrievePaymentOrderService - Servicio para recuperar órdenes de pago.
 */
@Service
public class RetrievePaymentOrderService implements RetrievePaymentOrderUseCase {

  private final PaymentOrderPort paymentOrderPort;

  public RetrievePaymentOrderService(PaymentOrderPort paymentOrderPort) {
    this.paymentOrderPort = paymentOrderPort;
  }

  @Override
  public Mono<PaymentOrderProcedure> retrieve(UUID paymentOrderId) {
    return paymentOrderPort.findById(paymentOrderId)
        .switchIfEmpty(Mono.error(new PaymentOrderNotFoundException(paymentOrderId)));
  }
}
