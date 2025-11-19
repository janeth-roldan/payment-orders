package com.banking.payment.orders.config;

import com.banking.payment.orders.domain.exception.InvalidPaymentOrderException;
import com.banking.payment.orders.domain.exception.PaymentOrderNotFoundException;
import java.net.URI;
import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

/**
 * GlobalExceptionHandler - Manejador global de excepciones según RFC 7807.
 * Usa ProblemDetail nativo de Spring 6+ para compatibilidad completa con RFC 7807.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Maneja PaymentOrderNotFoundException.
   */
  @ExceptionHandler(PaymentOrderNotFoundException.class)
  public ResponseEntity<ProblemDetail> handlePaymentOrderNotFound(
      PaymentOrderNotFoundException ex) {

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.NOT_FOUND,
        ex.getMessage()
    );
    problemDetail.setType(URI.create("https://api.bank.com/errors/not-found"));
    problemDetail.setTitle("Payment Order Not Found");
    problemDetail.setProperty("paymentOrderId", ex.getPaymentOrderId());
    problemDetail.setProperty("timestamp", OffsetDateTime.now());
    problemDetail.setInstance(URI.create("/payment-initiation/payment-orders/" 
        + ex.getPaymentOrderId()));

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
  }

  /**
   * Maneja InvalidPaymentOrderException.
   */
  @ExceptionHandler(InvalidPaymentOrderException.class)
  public ResponseEntity<ProblemDetail> handleInvalidPaymentOrder(
      InvalidPaymentOrderException ex) {

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST,
        ex.getMessage()
    );
    problemDetail.setType(URI.create("https://api.bank.com/errors/validation-error"));
    problemDetail.setTitle("Invalid Payment Order");
    problemDetail.setProperty("timestamp", OffsetDateTime.now());
    problemDetail.setInstance(URI.create("/payment-initiation/payment-orders"));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
  }

  /**
   * Maneja errores de validación de Bean Validation.
   */
  @ExceptionHandler(WebExchangeBindException.class)
  public ResponseEntity<ProblemDetail> handleValidationErrors(WebExchangeBindException ex) {

    String details = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .reduce((a, b) -> a + ", " + b)
        .orElse("Validation error");

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST,
        details
    );
    problemDetail.setType(URI.create("https://api.bank.com/errors/validation-error"));
    problemDetail.setTitle("Validation Error");
    problemDetail.setProperty("timestamp", OffsetDateTime.now());
    problemDetail.setInstance(URI.create("/payment-initiation/payment-orders"));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
  }

  /**
   * Maneja excepciones genéricas.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
    log.error("Unexpected error occurred", ex);

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "An unexpected error occurred"
    );
    problemDetail.setType(URI.create("https://api.bank.com/errors/internal-error"));
    problemDetail.setTitle("Internal Server Error");
    problemDetail.setProperty("timestamp", OffsetDateTime.now());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
  }
}
