package com.banking.payment.orders.application.service;

import com.banking.payment.orders.domain.exception.InvalidPaymentOrderException;
import com.banking.payment.orders.domain.model.DateInformation;
import com.banking.payment.orders.domain.model.Payee;
import com.banking.payment.orders.domain.model.Payer;
import com.banking.payment.orders.domain.model.PaymentDetails;
import com.banking.payment.orders.domain.model.PaymentOrderProcedure;
import com.banking.payment.orders.domain.port.in.InitiatePaymentOrderUseCase;
import com.banking.payment.orders.domain.port.out.PaymentOrderPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * InitiatePaymentOrderService - Servicio de aplicación para iniciar órdenes de pago.
 */
@Service
public class InitiatePaymentOrderService implements InitiatePaymentOrderUseCase {

  private final PaymentOrderPort paymentOrderPort;

  public InitiatePaymentOrderService(PaymentOrderPort paymentOrderPort) {
    this.paymentOrderPort = paymentOrderPort;
  }

  @Override
  public Mono<PaymentOrderProcedure> initiate(InitiatePaymentOrderCommand command) {
    return Mono.fromCallable(() -> createPaymentOrder(command))
        .flatMap(paymentOrderPort::save)
        .onErrorMap(IllegalArgumentException.class,
            ex -> new InvalidPaymentOrderException("Invalid payment order data", ex));
  }

  private PaymentOrderProcedure createPaymentOrder(InitiatePaymentOrderCommand command) {
    Payer payer = new Payer(
        command.payerReference(),
        command.payerBankReference(),
        command.payerProductInstanceReference()
    );

    Payee payee = new Payee(
        command.payeeReference(),
        command.payeeBankReference(),
        command.payeeProductInstanceReference()
    );

    PaymentDetails paymentDetails = new PaymentDetails(
        command.amount(),
        command.currency(),
        command.paymentMechanismType()
    );

    DateInformation dateInformation = new DateInformation(
        command.dateType(),
        command.date()
    );

    return PaymentOrderProcedure.initiate(
        command.paymentTransactionInitiatorReference(),
        payer,
        payee,
        paymentDetails,
        dateInformation,
        command.remittanceInformation()
    );
  }
}
