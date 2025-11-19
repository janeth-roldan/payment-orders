package com.banking.payment.orders.config;

import com.banking.payment.orders.domain.exception.InvalidPaymentOrderException;
import com.banking.payment.orders.domain.exception.PaymentOrderNotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GlobalExceptionHandlerTest - Tests para GlobalExceptionHandler con ProblemDetail.
 */
class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
  }

  @Test
  @DisplayName("Debe retornar 404 cuando se maneja PaymentOrderNotFoundException")
  void shouldReturn404WhenHandlingPaymentOrderNotFoundException() {
    // Given
    UUID orderId = UUID.randomUUID();
    PaymentOrderNotFoundException exception = new PaymentOrderNotFoundException(orderId);

    // When
    ResponseEntity<ProblemDetail> response = handler.handlePaymentOrderNotFound(exception);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(response.getBody().getTitle()).isEqualTo("Payment Order Not Found");
    assertThat(response.getBody().getDetail()).contains(orderId.toString());
    assertThat(response.getBody().getProperties()).containsKey("timestamp");
    assertThat(response.getBody().getProperties()).containsKey("paymentOrderId");
  }

  @Test
  @DisplayName("Debe retornar 400 cuando se maneja InvalidPaymentOrderException")
  void shouldReturn400WhenHandlingInvalidPaymentOrderException() {
    // Given
    InvalidPaymentOrderException exception = new InvalidPaymentOrderException(
        "Invalid payment data");

    // When
    ResponseEntity<ProblemDetail> response = handler.handleInvalidPaymentOrder(exception);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getBody().getTitle()).isEqualTo("Invalid Payment Order");
    assertThat(response.getBody().getDetail()).isEqualTo("Invalid payment data");
    assertThat(response.getBody().getProperties()).containsKey("timestamp");
  }

  @Test
  @DisplayName("Debe retornar 500 cuando se maneja excepción genérica")
  void shouldReturn500WhenHandlingGenericException() {
    // Given
    Exception exception = new RuntimeException("Unexpected error");

    // When
    ResponseEntity<ProblemDetail> response = handler.handleGenericException(exception);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
    assertThat(response.getBody().getDetail()).contains("An unexpected error occurred");
    assertThat(response.getBody().getProperties()).containsKey("timestamp");
  }

  @Test
  @DisplayName("Debe incluir timestamp en todas las respuestas de error")
  void shouldIncludeTimestampInAllErrorResponses() {
    // Given
    PaymentOrderNotFoundException exception = new PaymentOrderNotFoundException(
        UUID.randomUUID());

    // When
    ResponseEntity<ProblemDetail> response = handler.handlePaymentOrderNotFound(exception);

    // Then
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getProperties()).isNotNull();
    assertThat(response.getBody().getProperties()).containsKey("timestamp");
  }
}
