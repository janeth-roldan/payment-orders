package com.banking.payment.orders.application.service;

import com.banking.payment.orders.domain.exception.PaymentOrderNotFoundException;
import com.banking.payment.orders.domain.model.PaymentOrderProcedure;
import com.banking.payment.orders.domain.port.in.RetrievePaymentOrderStatusUseCase;
import com.banking.payment.orders.domain.port.out.PaymentOrderPort;
import java.util.UUID;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * RetrievePaymentOrderStatusService - Servicio para recuperar estado de órdenes.
 */
@Service
public class RetrievePaymentOrderStatusService implements RetrievePaymentOrderStatusUseCase {

  private final PaymentOrderPort paymentOrderPort;

  public RetrievePaymentOrderStatusService(PaymentOrderPort paymentOrderPort) {
    this.paymentOrderPort = paymentOrderPort;
  }

  @Override
  public Mono<PaymentOrderProcedure> retrieveStatus(UUID paymentOrderId) {
    return paymentOrderPort.findById(paymentOrderId)
        .switchIfEmpty(Mono.error(new PaymentOrderNotFoundException(paymentOrderId)));
  }
}
