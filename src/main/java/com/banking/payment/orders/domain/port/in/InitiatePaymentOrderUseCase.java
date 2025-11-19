package com.banking.payment.orders.domain.port.in;

import com.banking.payment.orders.domain.model.PaymentOrderProcedure;
import java.math.BigDecimal;
import java.time.LocalDate;
import reactor.core.publisher.Mono;

/**
 * InitiatePaymentOrderUseCase - Puerto de entrada para iniciar una orden de pago.
 */
public interface InitiatePaymentOrderUseCase {

  /**
   * Inicia una nueva orden de pago.
   *
   * @param command comando con los datos de la orden de pago
   * @return Mono con la orden de pago creada
   */
  Mono<PaymentOrderProcedure> initiate(InitiatePaymentOrderCommand command);

  /**
   * InitiatePaymentOrderCommand - Comando para iniciar una orden de pago.
   */
  record InitiatePaymentOrderCommand(
      String paymentTransactionInitiatorReference,
      String payerReference,
      String payerBankReference,
      String payerProductInstanceReference,
      String payeeReference,
      String payeeBankReference,
      String payeeProductInstanceReference,
      BigDecimal amount,
      String currency,
      String paymentMechanismType,
      String dateType,
      LocalDate date,
      String remittanceInformation
  ) {
  }
}
