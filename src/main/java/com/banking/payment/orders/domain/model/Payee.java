package com.banking.payment.orders.domain.model;

/**
 * Payee - Value Object para información del beneficiario/acreedor según BIAN.
 */
public record Payee(
    String payeeReference,
    String payeeBankReference,
    String payeeProductInstanceReference) {

  /**
   * Constructor con validaciones.
   */
  public Payee {
    if (payeeProductInstanceReference == null || payeeProductInstanceReference.isBlank()) {
      throw new IllegalArgumentException("payeeProductInstanceReference is required");
    }
  }
}
