package com.banking.payment.orders.adapter.out.persistence;

import com.banking.payment.orders.adapter.out.persistence.entity.PaymentOrderEntity;
import com.banking.payment.orders.adapter.out.persistence.mapper.PaymentOrderPersistenceMapper;
import com.banking.payment.orders.domain.model.DateInformation;
import com.banking.payment.orders.domain.model.Payee;
import com.banking.payment.orders.domain.model.Payer;
import com.banking.payment.orders.domain.model.PaymentDetails;
import com.banking.payment.orders.domain.model.PaymentOrderProcedure;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * PaymentOrderRepositoryAdapterTest - Tests unitarios para PaymentOrderRepositoryAdapter.
 */
@ExtendWith(MockitoExtension.class)
class PaymentOrderRepositoryAdapterTest {

  @Mock
  private PaymentOrderR2dbcRepository repository;

  @Mock
  private PaymentOrderPersistenceMapper mapper;

  @InjectMocks
  private PaymentOrderRepositoryAdapter adapter;

  private PaymentOrderProcedure domainObject;
  private PaymentOrderEntity entity;

  @BeforeEach
  void setUp() {
    domainObject = PaymentOrderProcedure.initiate(
        "TXN-12345",
        new Payer("John Doe", "BANK001", "ES9121000418450200051332"),
        new Payee("Jane Smith", "BANK002", "ES9121000418450200051333"),
        new PaymentDetails(new BigDecimal("150.00"), "EUR", "CreditTransfer"),
        new DateInformation("RequestedExecutionDate", LocalDate.now().plusDays(1)),
        "Payment for invoice INV-001"
    );

    entity = new PaymentOrderEntity();
    entity.setId(UUID.randomUUID());
    entity.setStatus("INITIATED");
  }

  @Test
  @DisplayName("Debe guardar nueva orden (INSERT)")
  void shouldInsertNewPaymentOrderWhenOrderDoesNotExist() {
    // Given
    when(repository.findById(any(UUID.class)))
        .thenReturn(Mono.empty());
    when(mapper.toEntity(any(PaymentOrderProcedure.class), any(Boolean.class)))
        .thenReturn(entity);
    when(repository.save(any(PaymentOrderEntity.class)))
        .thenReturn(Mono.just(entity));
    when(mapper.toDomain(any(PaymentOrderEntity.class)))
        .thenReturn(domainObject);

    // When
    Mono<PaymentOrderProcedure> result = adapter.save(domainObject);

    // Then
    StepVerifier.create(result)
        .assertNext(saved -> {
          assertThat(saved).isNotNull();
          assertThat(saved).isEqualTo(domainObject);
        })
        .verifyComplete();

    verify(repository).findById(any(UUID.class));
    verify(mapper).toEntity(any(PaymentOrderProcedure.class), any(Boolean.class));
    verify(repository).save(any(PaymentOrderEntity.class));
    verify(mapper).toDomain(any(PaymentOrderEntity.class));
  }

  @Test
  @DisplayName("Debe actualizar orden existente (UPDATE)")
  void shouldUpdateExistingPaymentOrderWhenOrderExists() {
    // Given
    when(repository.findById(any(UUID.class)))
        .thenReturn(Mono.just(entity));
    when(mapper.toEntity(any(PaymentOrderProcedure.class), any(Boolean.class)))
        .thenReturn(entity);
    when(repository.save(any(PaymentOrderEntity.class)))
        .thenReturn(Mono.just(entity));
    when(mapper.toDomain(any(PaymentOrderEntity.class)))
        .thenReturn(domainObject);

    // When
    Mono<PaymentOrderProcedure> result = adapter.save(domainObject);

    // Then
    StepVerifier.create(result)
        .assertNext(saved -> {
          assertThat(saved).isNotNull();
          assertThat(saved).isEqualTo(domainObject);
        })
        .verifyComplete();

    verify(repository).findById(any(UUID.class));
    verify(repository).save(any(PaymentOrderEntity.class));
  }

  @Test
  @DisplayName("Debe buscar orden por ID")
  void shouldFindPaymentOrderWhenIdExists() {
    // Given
    UUID orderId = UUID.randomUUID();
    when(repository.findById(orderId))
        .thenReturn(Mono.just(entity));
    when(mapper.toDomain(any(PaymentOrderEntity.class)))
        .thenReturn(domainObject);

    // When
    Mono<PaymentOrderProcedure> result = adapter.findById(orderId);

    // Then
    StepVerifier.create(result)
        .assertNext(found -> {
          assertThat(found).isNotNull();
          assertThat(found).isEqualTo(domainObject);
        })
        .verifyComplete();

    verify(repository).findById(orderId);
    verify(mapper).toDomain(any(PaymentOrderEntity.class));
  }

  @Test
  @DisplayName("Debe retornar vacío si orden no existe")
  void shouldReturnEmptyWhenPaymentOrderNotFound() {
    // Given
    UUID orderId = UUID.randomUUID();
    when(repository.findById(orderId))
        .thenReturn(Mono.empty());

    // When
    Mono<PaymentOrderProcedure> result = adapter.findById(orderId);

    // Then
    StepVerifier.create(result)
        .verifyComplete();

    verify(repository).findById(orderId);
  }
}
