package com.banking.payment.orders.application.service;

import com.banking.payment.orders.domain.model.DateInformation;
import com.banking.payment.orders.domain.model.Payee;
import com.banking.payment.orders.domain.model.Payer;
import com.banking.payment.orders.domain.model.PaymentDetails;
import com.banking.payment.orders.domain.model.PaymentOrderProcedure;
import com.banking.payment.orders.domain.port.in.InitiatePaymentOrderUseCase.InitiatePaymentOrderCommand;
import com.banking.payment.orders.domain.port.out.PaymentOrderPort;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * InitiatePaymentOrderServiceTest - Tests unitarios para InitiatePaymentOrderService.
 */
@ExtendWith(MockitoExtension.class)
class InitiatePaymentOrderServiceTest {

  @Mock
  private PaymentOrderPort paymentOrderPort;

  @InjectMocks
  private InitiatePaymentOrderService service;

  private InitiatePaymentOrderCommand validCommand;

  @BeforeEach
  void setUp() {
    validCommand = new InitiatePaymentOrderCommand(
        "TXN-12345",
        "John Doe",
        "BANK001",
        "ES9121000418450200051332",
        "Jane Smith",
        "BANK002",
        "ES9121000418450200051333",
        new BigDecimal("150.00"),
        "EUR",
        "CreditTransfer",
        "RequestedExecutionDate",
        LocalDate.now().plusDays(1),
        "Payment for invoice INV-001"
    );
  }

  @Test
  @DisplayName("Debe iniciar orden de pago exitosamente")
  void shouldInitiatePaymentOrderWhenValidCommandProvided() {
    // Given
    PaymentOrderProcedure savedOrder = PaymentOrderProcedure.initiate(
        validCommand.paymentTransactionInitiatorReference(),
        new Payer(
            validCommand.payerReference(),
            validCommand.payerBankReference(),
            validCommand.payerProductInstanceReference()
        ),
        new Payee(
            validCommand.payeeReference(),
            validCommand.payeeBankReference(),
            validCommand.payeeProductInstanceReference()
        ),
        new PaymentDetails(
            validCommand.amount(),
            validCommand.currency(),
            validCommand.paymentMechanismType()
        ),
        new DateInformation(
            validCommand.dateType(),
            validCommand.date()
        ),
        validCommand.remittanceInformation()
    );

    when(paymentOrderPort.save(any(PaymentOrderProcedure.class)))
        .thenReturn(Mono.just(savedOrder));

    // When
    Mono<PaymentOrderProcedure> result = service.initiate(validCommand);

    // Then
    StepVerifier.create(result)
        .assertNext(order -> {
          assertThat(order).isNotNull();
          assertThat(order.getPaymentOrderProcedureInstanceReference()).isNotNull();
          assertThat(order.getPaymentTransactionInitiatorReference())
              .isEqualTo(validCommand.paymentTransactionInitiatorReference());
          assertThat(order.getPayer().payerReference())
              .isEqualTo(validCommand.payerReference());
          assertThat(order.getPayee().payeeReference())
              .isEqualTo(validCommand.payeeReference());
          assertThat(order.getPaymentDetails().amount())
              .isEqualByComparingTo(validCommand.amount());
          assertThat(order.getPaymentDetails().currency())
              .isEqualTo(validCommand.currency());
        })
        .verifyComplete();

    verify(paymentOrderPort).save(any(PaymentOrderProcedure.class));
  }

  @Test
  @DisplayName("Debe propagar error si el puerto falla")
  void shouldPropagateErrorWhenPortFails() {
    // Given
    when(paymentOrderPort.save(any(PaymentOrderProcedure.class)))
        .thenReturn(Mono.error(new RuntimeException("Database error")));

    // When
    Mono<PaymentOrderProcedure> result = service.initiate(validCommand);

    // Then
    StepVerifier.create(result)
        .expectErrorMatches(throwable -> 
            throwable instanceof RuntimeException 
            && throwable.getMessage().equals("Database error"))
        .verify();
  }
}
