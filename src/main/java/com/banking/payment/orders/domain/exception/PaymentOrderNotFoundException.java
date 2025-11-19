package com.banking.payment.orders.domain.exception;

import java.util.UUID;

/**
 * PaymentOrderNotFoundException - Excepción cuando no se encuentra una orden de pago.
 */
public class PaymentOrderNotFoundException extends RuntimeException {

  private final UUID paymentOrderId;

  public PaymentOrderNotFoundException(UUID paymentOrderId) {
    super("Payment order not found: " + paymentOrderId);
    this.paymentOrderId = paymentOrderId;
  }

  public UUID getPaymentOrderId() {
    return paymentOrderId;
  }
}
