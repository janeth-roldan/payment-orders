package com.banking.payment.orders.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * PaymentOrderStatusTest - Tests unitarios para PaymentOrderStatus.
 */
class PaymentOrderStatusTest {

  @Test
  @DisplayName("Debe retornar status correcto cuando se usa fromValue con valor válido")
  void shouldReturnCorrectStatusWhenFromValueCalledWithValidValue() {
    // When & Then
    assertThat(PaymentOrderStatus.fromValue("Initiated"))
        .isEqualTo(PaymentOrderStatus.INITIATED);
    assertThat(PaymentOrderStatus.fromValue("Pending"))
        .isEqualTo(PaymentOrderStatus.PENDING);
    assertThat(PaymentOrderStatus.fromValue("Completed"))
        .isEqualTo(PaymentOrderStatus.COMPLETED);
    assertThat(PaymentOrderStatus.fromValue("Rejected"))
        .isEqualTo(PaymentOrderStatus.REJECTED);
    assertThat(PaymentOrderStatus.fromValue("Failed"))
        .isEqualTo(PaymentOrderStatus.FAILED);
    assertThat(PaymentOrderStatus.fromValue("Cancelled"))
        .isEqualTo(PaymentOrderStatus.CANCELLED);
  }

  @Test
  @DisplayName("Debe lanzar excepción cuando fromValue recibe valor inválido")
  void shouldThrowExceptionWhenFromValueCalledWithInvalidValue() {
    // When & Then
    assertThatThrownBy(() -> PaymentOrderStatus.fromValue("INVALID"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unknown PaymentOrderStatus: INVALID");
  }

  @Test
  @DisplayName("Debe retornar valor correcto cuando se llama getValue")
  void shouldReturnCorrectValueWhenGetValueCalled() {
    // When & Then
    assertThat(PaymentOrderStatus.INITIATED.getValue()).isEqualTo("Initiated");
    assertThat(PaymentOrderStatus.PENDING.getValue()).isEqualTo("Pending");
    assertThat(PaymentOrderStatus.COMPLETED.getValue()).isEqualTo("Completed");
    assertThat(PaymentOrderStatus.REJECTED.getValue()).isEqualTo("Rejected");
    assertThat(PaymentOrderStatus.FAILED.getValue()).isEqualTo("Failed");
    assertThat(PaymentOrderStatus.CANCELLED.getValue()).isEqualTo("Cancelled");
  }

  @Test
  @DisplayName("Debe ser case-sensitive al comparar valores")
  void shouldBeCaseSensitiveWhenComparingValues() {
    // When & Then
    assertThatThrownBy(() -> PaymentOrderStatus.fromValue("initiated"))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> PaymentOrderStatus.fromValue("INITIATED"))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
