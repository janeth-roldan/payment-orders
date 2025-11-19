package com.banking.payment.orders.domain.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * PaymentOrderProcedure - Control Record principal según BIAN 12.0.
 * Representa una orden de pago completa con toda su información.
 */
public class PaymentOrderProcedure {

  private UUID paymentOrderProcedureInstanceReference;
  private PaymentOrderStatus paymentOrderProcedureInstanceStatus;
  private String paymentTransactionInitiatorReference;
  private Payer payer;
  private Payee payee;
  private PaymentDetails paymentDetails;
  private DateInformation dateInformation;
  private String remittanceInformation;
  private OffsetDateTime createdDateTime;
  private OffsetDateTime lastUpdateDateTime;

  /**
   * Constructor privado para uso del Builder.
   */
  private PaymentOrderProcedure() {
  }

  /**
   * Crea una nueva orden de pago con estado INITIATED.
   *
   * @param paymentTransactionInitiatorReference referencia del iniciador
   * @param payer información del pagador
   * @param payee información del beneficiario
   * @param paymentDetails detalles del pago
   * @param dateInformation información de fechas
   * @param remittanceInformation información de remesa
   * @return nueva instancia de PaymentOrderProcedure
   */
  public static PaymentOrderProcedure initiate(
      String paymentTransactionInitiatorReference,
      Payer payer,
      Payee payee,
      PaymentDetails paymentDetails,
      DateInformation dateInformation,
      String remittanceInformation) {

    PaymentOrderProcedure procedure = new PaymentOrderProcedure();
    procedure.paymentOrderProcedureInstanceReference = UUID.randomUUID();
    procedure.paymentOrderProcedureInstanceStatus = PaymentOrderStatus.INITIATED;
    procedure.paymentTransactionInitiatorReference = paymentTransactionInitiatorReference;
    procedure.payer = payer;
    procedure.payee = payee;
    procedure.paymentDetails = paymentDetails;
    procedure.dateInformation = dateInformation;
    procedure.remittanceInformation = remittanceInformation;
    procedure.createdDateTime = OffsetDateTime.now();
    procedure.lastUpdateDateTime = OffsetDateTime.now();

    return procedure;
  }

  /**
   * Actualiza el estado de la orden de pago.
   *
   * @param newStatus nuevo estado
   */
  public void updateStatus(PaymentOrderStatus newStatus) {
    this.paymentOrderProcedureInstanceStatus = newStatus;
    this.lastUpdateDateTime = OffsetDateTime.now();
  }

  // Getters
  public UUID getPaymentOrderProcedureInstanceReference() {
    return paymentOrderProcedureInstanceReference;
  }

  public PaymentOrderStatus getPaymentOrderProcedureInstanceStatus() {
    return paymentOrderProcedureInstanceStatus;
  }

  public String getPaymentTransactionInitiatorReference() {
    return paymentTransactionInitiatorReference;
  }

  public Payer getPayer() {
    return payer;
  }

  public Payee getPayee() {
    return payee;
  }

  public PaymentDetails getPaymentDetails() {
    return paymentDetails;
  }

  public DateInformation getDateInformation() {
    return dateInformation;
  }

  public String getRemittanceInformation() {
    return remittanceInformation;
  }

  public OffsetDateTime getCreatedDateTime() {
    return createdDateTime;
  }

  public OffsetDateTime getLastUpdateDateTime() {
    return lastUpdateDateTime;
  }
}
