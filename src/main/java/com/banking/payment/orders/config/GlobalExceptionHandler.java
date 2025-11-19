package com.banking.payment.orders.config;

import com.banking.payment.orders.adapter.in.rest.model.ErrorResponse;
import com.banking.payment.orders.domain.exception.InvalidPaymentOrderException;
import com.banking.payment.orders.domain.exception.PaymentOrderNotFoundException;
import java.net.URI;
import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

/**
 * GlobalExceptionHandler - Manejador global de excepciones según RFC 7807.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Maneja PaymentOrderNotFoundException.
   */
  @ExceptionHandler(PaymentOrderNotFoundException.class)
  public ResponseEntity<ErrorResponse> handlePaymentOrderNotFound(
      PaymentOrderNotFoundException ex) {

    ErrorResponse error = new ErrorResponse();
    error.setType(URI.create("https://api.bank.com/errors/not-found"));
    error.setTitle("Payment Order Not Found");
    error.setStatus(HttpStatus.NOT_FOUND.value());
    error.setDetail(ex.getMessage());
    error.setInstance(URI.create("/payment-initiation/payment-orders/" + ex.getPaymentOrderId()));
    error.setTimestamp(OffsetDateTime.now());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  /**
   * Maneja InvalidPaymentOrderException.
   */
  @ExceptionHandler(InvalidPaymentOrderException.class)
  public ResponseEntity<ErrorResponse> handleInvalidPaymentOrder(
      InvalidPaymentOrderException ex) {

    ErrorResponse error = new ErrorResponse();
    error.setType(URI.create("https://api.bank.com/errors/validation-error"));
    error.setTitle("Invalid Payment Order");
    error.setStatus(HttpStatus.BAD_REQUEST.value());
    error.setDetail(ex.getMessage());
    error.setInstance(URI.create("/payment-initiation/payment-orders"));
    error.setTimestamp(OffsetDateTime.now());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  /**
   * Maneja errores de validación de Bean Validation.
   */
  @ExceptionHandler(WebExchangeBindException.class)
  public ResponseEntity<ErrorResponse> handleValidationErrors(WebExchangeBindException ex) {

    String details = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .reduce((a, b) -> a + ", " + b)
        .orElse("Validation error");

    ErrorResponse error = new ErrorResponse();
    error.setType(URI.create("https://api.bank.com/errors/validation-error"));
    error.setTitle("Validation Error");
    error.setStatus(HttpStatus.BAD_REQUEST.value());
    error.setDetail(details);
    error.setInstance(URI.create("/payment-initiation/payment-orders"));
    error.setTimestamp(OffsetDateTime.now());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  /**
   * Maneja excepciones genéricas.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    log.error("Unexpected error occurred", ex);

    ErrorResponse error = new ErrorResponse();
    error.setType(URI.create("https://api.bank.com/errors/internal-error"));
    error.setTitle("Internal Server Error");
    error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    error.setDetail("An unexpected error occurred: " + ex.getMessage());
    error.setTimestamp(OffsetDateTime.now());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}
