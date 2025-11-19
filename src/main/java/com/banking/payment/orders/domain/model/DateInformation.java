package com.banking.payment.orders.domain.model;

import java.time.LocalDate;

/**
 * DateInformation - Value Object para información de fechas según BIAN.
 */
public record DateInformation(
    String dateType,
    LocalDate date) {

  /**
   * Constructor con validaciones.
   */
  public DateInformation {
    if (dateType == null || dateType.isBlank()) {
      throw new IllegalArgumentException("dateType is required");
    }
    if (date == null) {
      throw new IllegalArgumentException("date is required");
    }
  }
}
