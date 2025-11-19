package com.banking.payment.orders.domain.exception;

/**
 * InvalidPaymentOrderException - Excepción para órdenes de pago inválidas.
 */
public class InvalidPaymentOrderException extends RuntimeException {

  public InvalidPaymentOrderException(String message) {
    super(message);
  }

  public InvalidPaymentOrderException(String message, Throwable cause) {
    super(message, cause);
  }
}
