package com.banking.payment.orders.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * InvalidPaymentOrderExceptionTest - Tests para InvalidPaymentOrderException.
 */
class InvalidPaymentOrderExceptionTest {

  @Test
  @DisplayName("Debe crear excepción con mensaje cuando se proporciona solo mensaje")
  void shouldCreateExceptionWithMessageWhenOnlyMessageProvided() {
    // Given
    String message = "Invalid payment order data";

    // When
    InvalidPaymentOrderException exception = new InvalidPaymentOrderException(message);

    // Then
    assertThat(exception).isNotNull();
    assertThat(exception.getMessage()).isEqualTo(message);
    assertThat(exception.getCause()).isNull();
    assertThat(exception).isInstanceOf(RuntimeException.class);
  }

  @Test
  @DisplayName("Debe crear excepción con mensaje y causa cuando ambos se proporcionan")
  void shouldCreateExceptionWithMessageAndCauseWhenBothProvided() {
    // Given
    String message = "Invalid payment order data";
    Throwable cause = new IllegalArgumentException("Amount cannot be negative");

    // When
    InvalidPaymentOrderException exception = new InvalidPaymentOrderException(message, cause);

    // Then
    assertThat(exception).isNotNull();
    assertThat(exception.getMessage()).isEqualTo(message);
    assertThat(exception.getCause()).isEqualTo(cause);
    assertThat(exception.getCause().getMessage()).isEqualTo("Amount cannot be negative");
  }

  @Test
  @DisplayName("Debe preservar la causa original de la excepción")
  void shouldPreserveOriginalCauseOfException() {
    // Given
    RuntimeException originalCause = new RuntimeException("Original error");
    InvalidPaymentOrderException exception = new InvalidPaymentOrderException(
        "Wrapper message", originalCause);

    // When & Then
    assertThat(exception.getCause()).isSameAs(originalCause);
  }
}
