package com.banking.payment.orders.application.service;

import com.banking.payment.orders.domain.exception.PaymentOrderNotFoundException;
import com.banking.payment.orders.domain.model.DateInformation;
import com.banking.payment.orders.domain.model.Payee;
import com.banking.payment.orders.domain.model.Payer;
import com.banking.payment.orders.domain.model.PaymentDetails;
import com.banking.payment.orders.domain.model.PaymentOrderProcedure;
import com.banking.payment.orders.domain.port.out.PaymentOrderPort;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * RetrievePaymentOrderServiceTest - Tests unitarios para RetrievePaymentOrderService.
 */
@ExtendWith(MockitoExtension.class)
class RetrievePaymentOrderServiceTest {

  @Mock
  private PaymentOrderPort paymentOrderPort;

  @InjectMocks
  private RetrievePaymentOrderService service;

  @Test
  @DisplayName("Debe recuperar orden de pago existente")
  void shouldRetrievePaymentOrderWhenOrderExists() {
    // Given
    UUID orderId = UUID.randomUUID();
    PaymentOrderProcedure existingOrder = PaymentOrderProcedure.initiate(
        "TXN-12345",
        new Payer("John Doe", "BANK001", "ES9121000418450200051332"),
        new Payee("Jane Smith", "BANK002", "ES9121000418450200051333"),
        new PaymentDetails(new BigDecimal("150.00"), "EUR", "CreditTransfer"),
        new DateInformation("RequestedExecutionDate", LocalDate.now().plusDays(1)),
        "Payment for invoice INV-001"
    );

    when(paymentOrderPort.findById(orderId))
        .thenReturn(Mono.just(existingOrder));

    // When
    Mono<PaymentOrderProcedure> result = service.retrieve(orderId);

    // Then
    StepVerifier.create(result)
        .assertNext(order -> {
          assertThat(order).isNotNull();
          assertThat(order.getPaymentTransactionInitiatorReference())
              .isEqualTo("TXN-12345");
          assertThat(order.getPayer().payerReference()).isEqualTo("John Doe");
          assertThat(order.getPayee().payeeReference()).isEqualTo("Jane Smith");
        })
        .verifyComplete();

    verify(paymentOrderPort).findById(orderId);
  }

  @Test
  @DisplayName("Debe lanzar excepción si orden no existe")
  void shouldThrowNotFoundExceptionWhenOrderDoesNotExist() {
    // Given
    UUID nonExistentId = UUID.randomUUID();
    when(paymentOrderPort.findById(nonExistentId))
        .thenReturn(Mono.empty());

    // When
    Mono<PaymentOrderProcedure> result = service.retrieve(nonExistentId);

    // Then
    StepVerifier.create(result)
        .expectError(PaymentOrderNotFoundException.class)
        .verify();

    verify(paymentOrderPort).findById(nonExistentId);
  }
}
