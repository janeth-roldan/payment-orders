package com.banking.payment.orders.adapter.out.persistence.mapper;

import com.banking.payment.orders.adapter.out.persistence.entity.PaymentOrderEntity;
import com.banking.payment.orders.domain.model.DateInformation;
import com.banking.payment.orders.domain.model.Payee;
import com.banking.payment.orders.domain.model.Payer;
import com.banking.payment.orders.domain.model.PaymentDetails;
import com.banking.payment.orders.domain.model.PaymentOrderProcedure;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PaymentOrderPersistenceMapperTest - Tests unitarios para PaymentOrderPersistenceMapper.
 */
class PaymentOrderPersistenceMapperTest {

  private PaymentOrderPersistenceMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new PaymentOrderPersistenceMapper();
  }

  @Test
  @DisplayName("Debe mapear domain a entity para INSERT")
  void shouldMapDomainToEntityForInsertWhenIsNewTrue() {
    // Given
    PaymentOrderProcedure domain = createDomainObject();

    // When
    PaymentOrderEntity entity = mapper.toEntity(domain, true);

    // Then
    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isEqualTo(domain.getPaymentOrderProcedureInstanceReference());
    assertThat(entity.isNew()).isTrue();
    assertThat(entity.getStatus()).isEqualTo("Initiated");
    assertThat(entity.getPaymentTransactionInitiatorReference()).isEqualTo("TXN-12345");
    assertThat(entity.getPayerReference()).isEqualTo("John Doe");
    assertThat(entity.getPayeeReference()).isEqualTo("Jane Smith");
    assertThat(entity.getAmount()).isEqualByComparingTo(new BigDecimal("150.00"));
    assertThat(entity.getCurrency()).isEqualTo("EUR");
  }

  @Test
  @DisplayName("Debe mapear domain a entity para UPDATE")
  void shouldMapDomainToEntityForUpdateWhenIsNewFalse() {
    // Given
    PaymentOrderProcedure domain = createDomainObject();

    // When
    PaymentOrderEntity entity = mapper.toEntity(domain, false);

    // Then
    assertThat(entity).isNotNull();
    assertThat(entity.isNew()).isFalse();
  }

  @Test
  @DisplayName("Debe mapear entity a domain")
  void shouldMapEntityToDomainWhenEntityProvided() {
    // Given
    PaymentOrderEntity entity = createEntity();

    // When
    PaymentOrderProcedure domain = mapper.toDomain(entity);

    // Then
    assertThat(domain).isNotNull();
    assertThat(domain.getPaymentOrderProcedureInstanceReference()).isEqualTo(entity.getId());
    assertThat(domain.getPaymentTransactionInitiatorReference())
        .isEqualTo(entity.getPaymentTransactionInitiatorReference());
    assertThat(domain.getPayer().payerReference()).isEqualTo(entity.getPayerReference());
    assertThat(domain.getPayee().payeeReference()).isEqualTo(entity.getPayeeReference());
    assertThat(domain.getPaymentDetails().amount()).isEqualByComparingTo(entity.getAmount());
    assertThat(domain.getPaymentDetails().currency()).isEqualTo(entity.getCurrency());
    assertThat(domain.getDateInformation().dateType()).isEqualTo(entity.getDateType());
    assertThat(domain.getDateInformation().date()).isEqualTo(entity.getDate());
  }

  @Test
  @DisplayName("Debe usar reflexión para setear ID correcto desde BD")
  void shouldSetCorrectIdFromDatabaseWhenMappingToDomain() {
    // Given
    UUID databaseId = UUID.randomUUID();
    PaymentOrderEntity entity = createEntity();
    entity.setId(databaseId);

    // When
    PaymentOrderProcedure domain = mapper.toDomain(entity);

    // Then
    assertThat(domain.getPaymentOrderProcedureInstanceReference())
        .isEqualTo(databaseId);
  }

  private PaymentOrderProcedure createDomainObject() {
    return PaymentOrderProcedure.initiate(
        "TXN-12345",
        new Payer("John Doe", "BANK001", "ES9121000418450200051332"),
        new Payee("Jane Smith", "BANK002", "ES9121000418450200051333"),
        new PaymentDetails(new BigDecimal("150.00"), "EUR", "CreditTransfer"),
        new DateInformation("RequestedExecutionDate", LocalDate.now().plusDays(1)),
        "Payment for invoice INV-001"
    );
  }

  private PaymentOrderEntity createEntity() {
    PaymentOrderEntity entity = new PaymentOrderEntity();
    entity.setId(UUID.randomUUID());
    entity.setStatus("Initiated");
    entity.setPaymentTransactionInitiatorReference("TXN-12345");
    entity.setPayerReference("John Doe");
    entity.setPayerBankReference("BANK001");
    entity.setPayerProductInstanceReference("ES9121000418450200051332");
    entity.setPayeeReference("Jane Smith");
    entity.setPayeeBankReference("BANK002");
    entity.setPayeeProductInstanceReference("ES9121000418450200051333");
    entity.setAmount(new BigDecimal("150.00"));
    entity.setCurrency("EUR");
    entity.setPaymentMechanismType("CreditTransfer");
    entity.setDateType("RequestedExecutionDate");
    entity.setDate(LocalDate.now().plusDays(1));
    entity.setRemittanceInformation("Payment for invoice INV-001");
    entity.setCreatedDateTime(OffsetDateTime.now());
    entity.setLastUpdateDateTime(OffsetDateTime.now());
    return entity;
  }
}
