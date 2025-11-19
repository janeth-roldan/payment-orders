package com.banking.payment.orders.domain.model;

/**
 * PaymentOrderStatus - Estados de una orden de pago según BIAN 12.0.
 */
public enum PaymentOrderStatus {
  INITIATED("Initiated"),
  PENDING("Pending"),
  ACCEPTED("Accepted"),
  IN_PROGRESS("InProgress"),
  COMPLETED("Completed"),
  SETTLED("Settled"),
  REJECTED("Rejected"),
  FAILED("Failed"),
  CANCELLED("Cancelled");

  private final String value;

  PaymentOrderStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  /**
   * Convierte un string a PaymentOrderStatus.
   *
   * @param value valor del estado
   * @return PaymentOrderStatus correspondiente
   */
  public static PaymentOrderStatus fromValue(String value) {
    for (PaymentOrderStatus status : PaymentOrderStatus.values()) {
      if (status.value.equals(value)) {
        return status;
      }
    }
    throw new IllegalArgumentException("Unknown PaymentOrderStatus: " + value);
  }
}
