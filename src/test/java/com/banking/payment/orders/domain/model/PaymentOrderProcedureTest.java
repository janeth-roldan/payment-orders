package com.banking.payment.orders.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PaymentOrderProcedureTest - Tests unitarios para PaymentOrderProcedure.
 */
class PaymentOrderProcedureTest {

  @Test
  @DisplayName("Debe crear orden de pago con estado INITIATED cuando se inicia")
  void shouldCreatePaymentOrderWithInitiatedStatusWhenInitiated() {
    // Given
    Payer payer = new Payer("John Doe", "BANK001", "ES9121000418450200051332");
    Payee payee = new Payee("Jane Smith", "BANK002", "ES9121000418450200051333");
    PaymentDetails paymentDetails = new PaymentDetails(
        new BigDecimal("150.00"), "EUR", "CreditTransfer");
    DateInformation dateInformation = new DateInformation(
        "RequestedExecutionDate", LocalDate.now().plusDays(1));

    // When
    PaymentOrderProcedure procedure = PaymentOrderProcedure.initiate(
        "TXN-12345",
        payer,
        payee,
        paymentDetails,
        dateInformation,
        "Payment for invoice INV-001"
    );

    // Then
    assertThat(procedure).isNotNull();
    assertThat(procedure.getPaymentOrderProcedureInstanceReference()).isNotNull();
    assertThat(procedure.getPaymentOrderProcedureInstanceStatus())
        .isEqualTo(PaymentOrderStatus.INITIATED);
    assertThat(procedure.getPaymentTransactionInitiatorReference()).isEqualTo("TXN-12345");
    assertThat(procedure.getPayer()).isEqualTo(payer);
    assertThat(procedure.getPayee()).isEqualTo(payee);
    assertThat(procedure.getPaymentDetails()).isEqualTo(paymentDetails);
    assertThat(procedure.getDateInformation()).isEqualTo(dateInformation);
    assertThat(procedure.getRemittanceInformation()).isEqualTo("Payment for invoice INV-001");
    assertThat(procedure.getCreatedDateTime()).isNotNull();
    assertThat(procedure.getLastUpdateDateTime()).isNotNull();
  }

  @Test
  @DisplayName("Debe actualizar estado cuando se llama updateStatus")
  void shouldUpdateStatusWhenUpdateStatusCalled() {
    // Given
    PaymentOrderProcedure procedure = createPaymentOrder();
    PaymentOrderStatus newStatus = PaymentOrderStatus.COMPLETED;

    // When
    procedure.updateStatus(newStatus);

    // Then
    assertThat(procedure.getPaymentOrderProcedureInstanceStatus()).isEqualTo(newStatus);
    assertThat(procedure.getLastUpdateDateTime()).isNotNull();
  }

  @Test
  @DisplayName("Debe mantener ID único para cada orden creada")
  void shouldMaintainUniqueIdForEachOrderCreated() {
    // Given & When
    PaymentOrderProcedure order1 = createPaymentOrder();
    PaymentOrderProcedure order2 = createPaymentOrder();

    // Then
    assertThat(order1.getPaymentOrderProcedureInstanceReference())
        .isNotEqualTo(order2.getPaymentOrderProcedureInstanceReference());
  }

  @Test
  @DisplayName("Debe crear timestamps diferentes para createdDateTime y lastUpdateDateTime")
  void shouldCreateDifferentTimestampsForCreatedAndLastUpdate() {
    // Given & When
    PaymentOrderProcedure procedure = createPaymentOrder();

    // Then
    assertThat(procedure.getCreatedDateTime()).isNotNull();
    assertThat(procedure.getLastUpdateDateTime()).isNotNull();
    assertThat(procedure.getCreatedDateTime())
        .isBeforeOrEqualTo(procedure.getLastUpdateDateTime());
  }

  private PaymentOrderProcedure createPaymentOrder() {
    return PaymentOrderProcedure.initiate(
        "TXN-12345",
        new Payer("John Doe", "BANK001", "ES9121000418450200051332"),
        new Payee("Jane Smith", "BANK002", "ES9121000418450200051333"),
        new PaymentDetails(new BigDecimal("150.00"), "EUR", "CreditTransfer"),
        new DateInformation("RequestedExecutionDate", LocalDate.now().plusDays(1)),
        "Payment for invoice INV-001"
    );
  }
}
