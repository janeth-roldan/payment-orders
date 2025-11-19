package com.banking.payment.orders.adapter.out.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * PaymentOrderEntity - Entidad de persistencia para órdenes de pago.
 */
@Table("payment_orders")
public class PaymentOrderEntity implements Persistable<UUID> {

  @Id
  @Column("id")
  private UUID id;
  
  @Transient
  private boolean isNew = true;

  @Column("status")
  private String status;

  @Column("payment_transaction_initiator_reference")
  private String paymentTransactionInitiatorReference;

  // Payer fields
  @Column("payer_reference")
  private String payerReference;

  @Column("payer_bank_reference")
  private String payerBankReference;

  @Column("payer_product_instance_reference")
  private String payerProductInstanceReference;

  // Payee fields
  @Column("payee_reference")
  private String payeeReference;

  @Column("payee_bank_reference")
  private String payeeBankReference;

  @Column("payee_product_instance_reference")
  private String payeeProductInstanceReference;

  // Payment Details fields
  @Column("amount")
  private BigDecimal amount;

  @Column("currency")
  private String currency;

  @Column("payment_mechanism_type")
  private String paymentMechanismType;

  // Date Information fields
  @Column("date_type")
  private String dateType;

  @Column("date")
  private LocalDate date;

  @Column("remittance_information")
  private String remittanceInformation;

  @Column("created_date_time")
  private OffsetDateTime createdDateTime;

  @Column("last_update_date_time")
  private OffsetDateTime lastUpdateDateTime;

  // Getters and Setters
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getPaymentTransactionInitiatorReference() {
    return paymentTransactionInitiatorReference;
  }

  public void setPaymentTransactionInitiatorReference(String paymentTransactionInitiatorReference) {
    this.paymentTransactionInitiatorReference = paymentTransactionInitiatorReference;
  }

  public String getPayerReference() {
    return payerReference;
  }

  public void setPayerReference(String payerReference) {
    this.payerReference = payerReference;
  }

  public String getPayerBankReference() {
    return payerBankReference;
  }

  public void setPayerBankReference(String payerBankReference) {
    this.payerBankReference = payerBankReference;
  }

  public String getPayerProductInstanceReference() {
    return payerProductInstanceReference;
  }

  public void setPayerProductInstanceReference(String payerProductInstanceReference) {
    this.payerProductInstanceReference = payerProductInstanceReference;
  }

  public String getPayeeReference() {
    return payeeReference;
  }

  public void setPayeeReference(String payeeReference) {
    this.payeeReference = payeeReference;
  }

  public String getPayeeBankReference() {
    return payeeBankReference;
  }

  public void setPayeeBankReference(String payeeBankReference) {
    this.payeeBankReference = payeeBankReference;
  }

  public String getPayeeProductInstanceReference() {
    return payeeProductInstanceReference;
  }

  public void setPayeeProductInstanceReference(String payeeProductInstanceReference) {
    this.payeeProductInstanceReference = payeeProductInstanceReference;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getPaymentMechanismType() {
    return paymentMechanismType;
  }

  public void setPaymentMechanismType(String paymentMechanismType) {
    this.paymentMechanismType = paymentMechanismType;
  }

  public String getDateType() {
    return dateType;
  }

  public void setDateType(String dateType) {
    this.dateType = dateType;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public String getRemittanceInformation() {
    return remittanceInformation;
  }

  public void setRemittanceInformation(String remittanceInformation) {
    this.remittanceInformation = remittanceInformation;
  }

  public OffsetDateTime getCreatedDateTime() {
    return createdDateTime;
  }

  public void setCreatedDateTime(OffsetDateTime createdDateTime) {
    this.createdDateTime = createdDateTime;
  }

  public OffsetDateTime getLastUpdateDateTime() {
    return lastUpdateDateTime;
  }

  public void setLastUpdateDateTime(OffsetDateTime lastUpdateDateTime) {
    this.lastUpdateDateTime = lastUpdateDateTime;
  }
  
  // Persistable methods
  @Override
  public boolean isNew() {
    return isNew;
  }
  
  public void setNew(boolean isNew) {
    this.isNew = isNew;
  }
}
