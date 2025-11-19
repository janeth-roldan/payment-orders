package com.banking.payment.orders.domain.exception;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PaymentOrderNotFoundExceptionTest - Tests para PaymentOrderNotFoundException.
 */
class PaymentOrderNotFoundExceptionTest {

  @Test
  @DisplayName("Debe crear excepción con mensaje correcto cuando se proporciona UUID")
  void shouldCreateExceptionWithCorrectMessageWhenUuidProvided() {
    // Given
    UUID orderId = UUID.randomUUID();

    // When
    PaymentOrderNotFoundException exception = new PaymentOrderNotFoundException(orderId);

    // Then
    assertThat(exception).isNotNull();
    assertThat(exception.getMessage())
        .isEqualTo("Payment order not found: " + orderId);
    assertThat(exception).isInstanceOf(RuntimeException.class);
  }

  @Test
  @DisplayName("Debe incluir UUID en el mensaje de error")
  void shouldIncludeUuidInErrorMessage() {
    // Given
    UUID orderId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    // When
    PaymentOrderNotFoundException exception = new PaymentOrderNotFoundException(orderId);

    // Then
    assertThat(exception.getMessage())
        .contains("123e4567-e89b-12d3-a456-426614174000");
  }
}
