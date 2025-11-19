package com.banking.payment.orders.domain.model;

/**
 * Payer - Value Object para información del pagador/deudor según BIAN.
 */
public record Payer(
    String payerReference,
    String payerBankReference,
    String payerProductInstanceReference) {

  /**
   * Constructor con validaciones.
   */
  public Payer {
    if (payerProductInstanceReference == null || payerProductInstanceReference.isBlank()) {
      throw new IllegalArgumentException("payerProductInstanceReference is required");
    }
  }
}
