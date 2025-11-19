package com.banking.payment.orders.domain.model;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PaymentDetailsTest - Tests para PaymentDetails value object.
 */
class PaymentDetailsTest {

  @Test
  @DisplayName("Debe crear PaymentDetails con todos los campos correctos")
  void shouldCreatePaymentDetailsWithAllFieldsCorrect() {
    // Given
    BigDecimal amount = new BigDecimal("150.00");
    String currency = "EUR";
    String paymentMechanismType = "CreditTransfer";

    // When
    PaymentDetails paymentDetails = new PaymentDetails(amount, currency, paymentMechanismType);

    // Then
    assertThat(paymentDetails).isNotNull();
    assertThat(paymentDetails.amount()).isEqualByComparingTo(amount);
    assertThat(paymentDetails.currency()).isEqualTo(currency);
    assertThat(paymentDetails.paymentMechanismType()).isEqualTo(paymentMechanismType);
  }

  @Test
  @DisplayName("Debe ser igual cuando todos los campos son iguales")
  void shouldBeEqualWhenAllFieldsAreEqual() {
    // Given
    PaymentDetails details1 = new PaymentDetails(
        new BigDecimal("150.00"), "EUR", "CreditTransfer");
    PaymentDetails details2 = new PaymentDetails(
        new BigDecimal("150.00"), "EUR", "CreditTransfer");

    // When & Then
    assertThat(details1).isEqualTo(details2);
    assertThat(details1.hashCode()).isEqualTo(details2.hashCode());
  }

  @Test
  @DisplayName("Debe ser diferente cuando los campos difieren")
  void shouldBeDifferentWhenFieldsDiffer() {
    // Given
    PaymentDetails details1 = new PaymentDetails(
        new BigDecimal("150.00"), "EUR", "CreditTransfer");
    PaymentDetails details2 = new PaymentDetails(
        new BigDecimal("200.00"), "USD", "DirectDebit");

    // When & Then
    assertThat(details1).isNotEqualTo(details2);
  }
}
