package com.banking.payment.orders.domain.model;

import java.math.BigDecimal;

/**
 * PaymentDetails - Value Object para detalles del pago según BIAN.
 */
public record PaymentDetails(
    BigDecimal amount,
    String currency,
    String paymentMechanismType) {

  /**
   * Constructor con validaciones.
   */
  public PaymentDetails {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("amount must be greater than zero");
    }
    if (currency == null || currency.isBlank()) {
      throw new IllegalArgumentException("currency is required");
    }
    if (!currency.matches("^[A-Z]{3}$")) {
      throw new IllegalArgumentException("currency must be a valid ISO 4217 code");
    }
  }
}
